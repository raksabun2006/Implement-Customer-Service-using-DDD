package co.istad.dto;

import co.istad.valueObject.CustomerSegment;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.util.UUID;

public record UpdateCustomerCommand(
        @NotNull(message = "Customer ID is required")
        UUID customerId,

        @NotBlank(message = "Family name is required")
        String familyName,

        @NotBlank(message = "Given name is required")
        String givenName,

        CustomerSegment segment
) {
    public UpdateCustomerCommand(UUID customerId, String familyName, String givenName) {
        this(customerId, familyName, givenName, null);
    }
}
