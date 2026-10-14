package co.istad.payment.dto;

import co.istad.payment.domain.valueobject.PaymentStatus;

import java.util.List;
import java.util.UUID;

public record ProcessPaymentResult(
        UUID paymentId,
        UUID customerId,
        UUID orderId,
        PaymentStatus paymentStatus,
        List<String> failureMessages
) {
}
