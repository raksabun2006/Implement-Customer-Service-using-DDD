package co.istad.order.dto;

import co.istad.order.domain.valueobject.OrderStatus;
import java.util.UUID;

public record CreateOrderResponse(
        UUID orderId,
        UUID orderTrackingId,
        OrderStatus orderStatus,
        String message
) {
}
