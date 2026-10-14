package co.istad.business.dto.register;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.math.BigDecimal;
import java.util.UUID;

public record ProductCommand(
        UUID productId,

        @NotBlank(message = "Product name is required")
        String name,

        @NotNull(message = "Product price is required")
        @Positive(message = "Product price must be positive")
        BigDecimal price,

        boolean available
) {
}
