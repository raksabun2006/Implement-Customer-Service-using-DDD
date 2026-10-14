package co.istad.order.dto;

import co.istad.order.domain.valueobject.OrderStatus;
import java.util.List;
import java.util.UUID;

public record TrackOrderResponse(
        UUID orderTrackingId,
        OrderStatus orderStatus,
        List<String> failureMessages
) {
}
