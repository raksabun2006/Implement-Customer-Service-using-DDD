package co.istad.payment.mapper;

import co.istad.CustomerId;
import co.istad.Money;
import co.istad.payment.document.CreditEntryDocument;
import co.istad.payment.document.CreditHistoryDocument;
import co.istad.payment.document.PaymentDocument;
import co.istad.payment.domain.entity.CreditEntry;
import co.istad.payment.domain.entity.CreditHistory;
import co.istad.payment.domain.entity.Payment;
import co.istad.payment.domain.valueobject.CreditEntryId;
import co.istad.payment.domain.valueobject.CreditHistoryId;
import co.istad.payment.domain.valueobject.OrderId;
import co.istad.payment.domain.valueobject.PaymentId;
import org.springframework.stereotype.Component;

@Component
public class PaymentPersistenceMapper {

    public PaymentDocument toDocument(Payment payment) {
        if (payment == null) {
            return null;
        }

        return PaymentDocument.builder()
                .id(payment.getId() != null ? payment.getId().value() : null)
                .customerId(payment.getCustomerId() != null ? payment.getCustomerId().value() : null)
                .orderId(payment.getOrderId() != null ? payment.getOrderId().value() : null)
                .price(payment.getPrice() != null ? payment.getPrice().amount() : null)
                .paymentStatus(payment.getPaymentStatus())
                .createdAt(payment.getCreatedAt())
                .build();
    }

    public Payment toDomain(PaymentDocument document) {
        if (document == null) {
            return null;
        }

        return Payment.builder()
                .id(document.getId() != null ? new PaymentId(document.getId()) : null)
                .customerId(document.getCustomerId() != null ? new CustomerId(document.getCustomerId()) : null)
                .orderId(document.getOrderId() != null ? new OrderId(document.getOrderId()) : null)
                .price(document.getPrice() != null ? new Money(document.getPrice()) : null)
                .paymentStatus(document.getPaymentStatus())
                .createdAt(document.getCreatedAt())
                .build();
    }

    public CreditEntryDocument toDocument(CreditEntry creditEntry) {
        if (creditEntry == null) {
            return null;
        }

        return CreditEntryDocument.builder()
                .id(creditEntry.getId() != null ? creditEntry.getId().value() : null)
                .customerId(creditEntry.getCustomerId() != null ? creditEntry.getCustomerId().value() : null)
                .totalCreditAmount(creditEntry.getTotalCreditAmount() != null ? creditEntry.getTotalCreditAmount().amount() : null)
                .build();
    }

    public CreditEntry toDomain(CreditEntryDocument document) {
        if (document == null) {
            return null;
        }

        return new CreditEntry(
                document.getId() != null ? new CreditEntryId(document.getId()) : null,
                document.getCustomerId() != null ? new CustomerId(document.getCustomerId()) : null,
                document.getTotalCreditAmount() != null ? new Money(document.getTotalCreditAmount()) : null
        );
    }

    public CreditHistoryDocument toDocument(CreditHistory creditHistory) {
        if (creditHistory == null) {
            return null;
        }

        return CreditHistoryDocument.builder()
                .id(creditHistory.getId() != null ? creditHistory.getId().value() : null)
                .customerId(creditHistory.getCustomerId() != null ? creditHistory.getCustomerId().value() : null)
                .amount(creditHistory.getAmount() != null ? creditHistory.getAmount().amount() : null)
                .transactionType(creditHistory.getTransactionType())
                .build();
    }

    public CreditHistory toDomain(CreditHistoryDocument document) {
        if (document == null) {
            return null;
        }

        return new CreditHistory(
                document.getId() != null ? new CreditHistoryId(document.getId()) : null,
                document.getCustomerId() != null ? new CustomerId(document.getCustomerId()) : null,
                document.getAmount() != null ? new Money(document.getAmount()) : null,
                document.getTransactionType()
        );
    }
}
