package co.istad.business.dto;

import java.util.List;
import java.util.UUID;

public record RegisterBusinessResponse(
        UUID businessId,
        String name,
        String email,
        String phone,
        String address,
        boolean active,
        List<ProductResponse> products,
        String message
) {
}
