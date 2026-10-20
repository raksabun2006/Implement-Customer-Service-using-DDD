package co.istad.dto;

import jakarta.validation.constraints.NotNull;

import java.util.UUID;

public record DeleteCustomerCommand(
        @NotNull(message = "Customer ID is required")
        UUID customerId
) {
}
