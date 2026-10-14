package co.istad.order.dto.track;

import jakarta.validation.constraints.NotNull;
import java.util.UUID;

public record TrackOrderQuery(
        @NotNull(message = "Order tracking ID is required")
        UUID orderTrackingId
) {
}
