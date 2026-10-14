package co.istad.business.dto;

import java.math.BigDecimal;
import java.util.UUID;

public record ProductResponse(
        UUID productId,
        String name,
        BigDecimal price,
        boolean available
) {
}
