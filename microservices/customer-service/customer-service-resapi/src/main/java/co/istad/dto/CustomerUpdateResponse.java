package co.istad.dto;

import java.util.UUID;

public record CustomerUpdateResponse(
        UUID customerId,
        String username,
        String familyName,
        String givenName,
        String email,
        String phoneNumber
) {
}
