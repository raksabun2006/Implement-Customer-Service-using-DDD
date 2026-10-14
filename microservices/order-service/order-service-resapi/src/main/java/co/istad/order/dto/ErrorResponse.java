package co.istad.order.dto;

import java.time.ZonedDateTime;
import java.util.List;

public record ErrorResponse(
        int status,
        String message,
        List<String> errors,
        ZonedDateTime timestamp
) {
}
