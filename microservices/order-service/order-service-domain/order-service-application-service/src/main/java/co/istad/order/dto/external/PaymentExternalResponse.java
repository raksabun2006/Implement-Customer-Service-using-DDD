package co.istad.order.dto.external;

import java.util.List;
import java.util.UUID;

public record PaymentExternalResponse(
        UUID paymentId,
        UUID customerId,
        UUID orderId,
        String paymentStatus,
        List<String> failureMessages
) {
}
