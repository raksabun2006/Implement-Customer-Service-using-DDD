package co.istad.order.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record OrderAddressRequest(
        @NotBlank(message = "Street is required")
        @Size(max = 50, message = "Street cannot exceed 50 characters")
        String street,

        @NotBlank(message = "Postal code is required")
        @Size(max = 10, message = "Postal code cannot exceed 10 characters")
        String postalCode,

        @NotBlank(message = "City is required")
        @Size(max = 50, message = "City cannot exceed 50 characters")
        String city
) {
}
