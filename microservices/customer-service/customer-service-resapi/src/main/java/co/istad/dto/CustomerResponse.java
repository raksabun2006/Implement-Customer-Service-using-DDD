package co.istad.dto;

import co.istad.valueObject.CustomerSegment;
import co.istad.valueObject.CustomerStatus;

import java.time.Instant;
import java.util.UUID;

public record CustomerResponse(
        UUID customerId,
        String username,
        String familyName,
        String givenName,
        String email,
        String phoneNumber,
        CustomerStatus status,
        CustomerSegment segment,
        Instant createdAt
) {
    public CustomerResponse(UUID customerId, String username, String familyName, String givenName, String email, String phoneNumber, CustomerStatus status) {
        this(customerId, username, familyName, givenName, email, phoneNumber, status, CustomerSegment.SILVER, Instant.now());
    }
}
