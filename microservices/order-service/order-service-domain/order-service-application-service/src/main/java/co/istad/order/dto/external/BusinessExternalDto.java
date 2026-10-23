package co.istad.order.dto.external;

import java.util.List;
import java.util.UUID;

public record BusinessExternalDto(
        UUID businessId,
        String name,
        boolean active,
        List<ProductExternalDto> products
) {
}
