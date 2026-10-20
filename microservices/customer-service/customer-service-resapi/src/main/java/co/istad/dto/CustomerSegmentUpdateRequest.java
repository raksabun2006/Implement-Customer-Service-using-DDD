package co.istad.dto;

import co.istad.valueObject.CustomerSegment;
import jakarta.validation.constraints.NotNull;

public record CustomerSegmentUpdateRequest(
        @NotNull(message = "Customer segment is required")
        CustomerSegment segment
) {
}
