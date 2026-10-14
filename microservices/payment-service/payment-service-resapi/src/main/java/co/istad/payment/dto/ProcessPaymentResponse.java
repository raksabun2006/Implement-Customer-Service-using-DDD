package co.istad.payment.dto;

import co.istad.payment.domain.valueobject.PaymentStatus;

import java.util.List;
import java.util.UUID;

public record ProcessPaymentResponse(
        UUID paymentId,
        UUID customerId,
        UUID orderId,
        PaymentStatus paymentStatus,
        List<String> failureMessages
) {
}
