package co.istad.business.dto.register;

import java.util.List;
import java.util.UUID;

public record RegisterBusinessResult(
        UUID businessId,
        String name,
        String email,
        String phone,
        String address,
        boolean active,
        List<ProductResult> products,
        String message
) {
}
