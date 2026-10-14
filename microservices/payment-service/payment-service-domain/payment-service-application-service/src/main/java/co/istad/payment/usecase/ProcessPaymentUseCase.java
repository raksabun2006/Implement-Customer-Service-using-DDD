package co.istad.payment.usecase;

import co.istad.CustomerId;
import co.istad.Money;
import co.istad.payment.domain.entity.CreditEntry;
import co.istad.payment.domain.entity.CreditHistory;
import co.istad.payment.domain.entity.Payment;
import co.istad.payment.domain.event.PaymentEvent;
import co.istad.payment.domain.service.PaymentDomainService;
import co.istad.payment.domain.valueobject.OrderId;
import co.istad.payment.domain.valueobject.PaymentStatus;
import co.istad.payment.dto.ProcessPaymentCommand;
import co.istad.payment.dto.ProcessPaymentResult;
import co.istad.payment.port.out.CreditEntryRepository;
import co.istad.payment.port.out.CreditHistoryRepository;
import co.istad.payment.port.out.PaymentRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class ProcessPaymentUseCase {

    private final PaymentDomainService paymentDomainService;
    private final PaymentRepository paymentRepository;
    private final CreditEntryRepository creditEntryRepository;
    private final CreditHistoryRepository creditHistoryRepository;

    public ProcessPaymentResult execute(ProcessPaymentCommand command) {
        log.info("Processing payment for order id: {} and customer id: {}", command.orderId(), command.customerId());

        Payment payment = Payment.builder()
                .customerId(new CustomerId(command.customerId()))
                .orderId(new OrderId(command.orderId()))
                .price(new Money(command.price()))
                .build();

        List<String> failureMessages = new ArrayList<>();
        CustomerId customerId = payment.getCustomerId();

        CreditEntry creditEntry = creditEntryRepository.findByCustomerId(customerId)
                .orElse(null);

        List<CreditHistory> creditHistories = new ArrayList<>(creditHistoryRepository.findByCustomerId(customerId));

        PaymentEvent paymentEvent = paymentDomainService.validateAndInitiatePayment(
                payment, creditEntry, creditHistories, failureMessages
        );

        if (failureMessages.isEmpty() && creditEntry != null) {
            creditEntryRepository.save(creditEntry);
            creditHistoryRepository.saveAll(creditHistories);
        }

        Payment savedPayment = paymentRepository.save(payment);
        log.info("Payment processed with status: {} for id: {}", savedPayment.getPaymentStatus(), savedPayment.getId().value());

        return new ProcessPaymentResult(
                savedPayment.getId().value(),
                savedPayment.getCustomerId().value(),
                savedPayment.getOrderId().value(),
                savedPayment.getPaymentStatus(),
                paymentEvent.getFailureMessages()
        );
    }
}
