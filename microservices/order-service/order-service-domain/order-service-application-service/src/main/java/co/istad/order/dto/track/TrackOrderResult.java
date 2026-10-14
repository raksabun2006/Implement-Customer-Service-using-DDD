package co.istad.order.dto.track;

import co.istad.order.domain.valueobject.OrderStatus;
import java.util.List;
import java.util.UUID;

public record TrackOrderResult(
        UUID orderTrackingId,
        OrderStatus orderStatus,
        List<String> failureMessages
) {
}
