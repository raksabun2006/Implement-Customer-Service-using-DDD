package co.istad.order.dto.create;

import co.istad.order.domain.valueobject.OrderStatus;
import java.util.UUID;

public record CreateOrderResult(
        UUID orderId,
        UUID orderTrackingId,
        OrderStatus orderStatus,
        String message
) {
}
