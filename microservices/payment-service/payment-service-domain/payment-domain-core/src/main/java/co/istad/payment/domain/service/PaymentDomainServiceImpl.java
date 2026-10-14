package co.istad.payment.domain.service;

import co.istad.Money;
import co.istad.payment.domain.entity.CreditEntry;
import co.istad.payment.domain.entity.CreditHistory;
import co.istad.payment.domain.entity.Payment;
import co.istad.payment.domain.event.PaymentCancelledEvent;
import co.istad.payment.domain.event.PaymentCompletedEvent;
import co.istad.payment.domain.event.PaymentEvent;
import co.istad.payment.domain.event.PaymentFailedEvent;
import co.istad.payment.domain.valueobject.CreditHistoryId;
import co.istad.payment.domain.valueobject.PaymentStatus;
import co.istad.payment.domain.valueobject.TransactionType;

import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.List;
import java.util.UUID;

public class PaymentDomainServiceImpl implements PaymentDomainService {

    private static final String UTC = "UTC";

    @Override
    public PaymentEvent validateAndInitiatePayment(Payment payment,
                                                  CreditEntry creditEntry,
                                                  List<CreditHistory> creditHistories,
                                                  List<String> failureMessages) {
        payment.validatePayment(failureMessages);
        payment.initializePayment();
        validateCreditEntry(payment, creditEntry, failureMessages);
        subtractCreditEntry(payment, creditEntry);
        updateCreditHistory(payment, creditHistories, TransactionType.DEBIT);
        validateCreditHistory(creditEntry, creditHistories, failureMessages);

        if (failureMessages.isEmpty()) {
            payment.updateStatus(PaymentStatus.COMPLETED);
            return new PaymentCompletedEvent(payment, ZonedDateTime.now(ZoneId.of(UTC)));
        } else {
            payment.updateStatus(PaymentStatus.FAILED);
            return new PaymentFailedEvent(payment, ZonedDateTime.now(ZoneId.of(UTC)), failureMessages);
        }
    }

    @Override
    public PaymentEvent validateAndCancelPayment(Payment payment,
                                                 CreditEntry creditEntry,
                                                 List<CreditHistory> creditHistories,
                                                 List<String> failureMessages) {
        payment.validatePayment(failureMessages);
        addCreditEntry(payment, creditEntry);
        updateCreditHistory(payment, creditHistories, TransactionType.CREDIT);

        if (failureMessages.isEmpty()) {
            payment.updateStatus(PaymentStatus.CANCELLED);
            return new PaymentCancelledEvent(payment, ZonedDateTime.now(ZoneId.of(UTC)));
        } else {
            payment.updateStatus(PaymentStatus.FAILED);
            return new PaymentFailedEvent(payment, ZonedDateTime.now(ZoneId.of(UTC)), failureMessages);
        }
    }

    private void validateCreditEntry(Payment payment, CreditEntry creditEntry, List<String> failureMessages) {
        if (creditEntry == null) {
            failureMessages.add("Could not find credit entry for customer: " + payment.getCustomerId().value());
            return;
        }
        if (payment.getPrice().isGreaterThan(creditEntry.getTotalCreditAmount())) {
            failureMessages.add("Customer: " + payment.getCustomerId().value() + " does not have enough credit for payment!");
        }
    }

    private void subtractCreditEntry(Payment payment, CreditEntry creditEntry) {
        if (creditEntry != null && !payment.getPrice().isGreaterThan(creditEntry.getTotalCreditAmount())) {
            creditEntry.subtractCreditAmount(payment.getPrice());
        }
    }

    private void addCreditEntry(Payment payment, CreditEntry creditEntry) {
        if (creditEntry != null) {
            creditEntry.addCreditAmount(payment.getPrice());
        }
    }

    private void updateCreditHistory(Payment payment, List<CreditHistory> creditHistories, TransactionType transactionType) {
        creditHistories.add(new CreditHistory(
                new CreditHistoryId(UUID.randomUUID()),
                payment.getCustomerId(),
                payment.getPrice(),
                transactionType
        ));
    }

    private void validateCreditHistory(CreditEntry creditEntry, List<CreditHistory> creditHistories, List<String> failureMessages) {
        if (creditEntry == null) {
            return;
        }

        Money totalHistoryAmount = getTotalHistoryAmount(creditHistories, TransactionType.CREDIT)
                .subtract(getTotalHistoryAmount(creditHistories, TransactionType.DEBIT));

        if (!creditEntry.getTotalCreditAmount().equals(totalHistoryAmount)) {
            failureMessages.add("Credit history total is not equal to current credit for customer: " +
                    creditEntry.getCustomerId().value() + "!");
        }
    }

    private Money getTotalHistoryAmount(List<CreditHistory> creditHistories, TransactionType transactionType) {
        return creditHistories.stream()
                .filter(history -> history.getTransactionType() == transactionType)
                .map(CreditHistory::getAmount)
                .reduce(Money.ZERO, Money::add);
    }
}
