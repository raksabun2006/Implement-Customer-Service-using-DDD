package co.istad.order.dto.external;

import java.math.BigDecimal;
import java.util.UUID;

public record ProductExternalDto(
        UUID productId,
        String name,
        BigDecimal price,
        boolean available
) {
}
