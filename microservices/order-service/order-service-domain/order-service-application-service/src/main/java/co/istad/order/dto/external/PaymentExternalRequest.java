package co.istad.order.dto.external;

import java.math.BigDecimal;
import java.util.UUID;

public record PaymentExternalRequest(
        UUID customerId,
        UUID orderId,
        BigDecimal price
) {
}
