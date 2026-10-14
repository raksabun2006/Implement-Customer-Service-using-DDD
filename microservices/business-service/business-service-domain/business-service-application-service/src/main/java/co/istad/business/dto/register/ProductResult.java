package co.istad.business.dto.register;

import java.math.BigDecimal;
import java.util.UUID;

public record ProductResult(
        UUID productId,
        String name,
        BigDecimal price,
        boolean available
) {
}
