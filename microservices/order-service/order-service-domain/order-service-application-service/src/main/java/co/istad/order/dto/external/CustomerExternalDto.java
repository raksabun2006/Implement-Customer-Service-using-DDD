package co.istad.order.dto.external;

import java.util.UUID;

public record CustomerExternalDto(
        UUID customerId,
        String username,
        String email,
        String status
) {
}
