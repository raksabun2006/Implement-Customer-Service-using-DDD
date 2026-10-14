package co.istad.payment.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.math.BigDecimal;
import java.util.UUID;

public record ProcessPaymentCommand(
        @NotNull(message = "Customer ID is required")
        UUID customerId,

        @NotNull(message = "Order ID is required")
        UUID orderId,

        @NotNull(message = "Price is required")
        @Positive(message = "Price must be positive")
        BigDecimal price
) {
}
