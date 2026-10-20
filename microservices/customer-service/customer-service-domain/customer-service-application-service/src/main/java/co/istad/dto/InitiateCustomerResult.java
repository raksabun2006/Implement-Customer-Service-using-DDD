package co.istad.dto;

import co.istad.valueObject.CustomerSegment;
import co.istad.valueObject.CustomerStatus;

import java.time.Instant;
import java.util.UUID;

public record InitiateCustomerResult(
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
    public InitiateCustomerResult(UUID customerId, String username, String familyName, String givenName, String email, String phoneNumber) {
        this(customerId, username, familyName, givenName, email, phoneNumber, CustomerStatus.ACTIVE, CustomerSegment.SILVER, Instant.now());
    }
}
