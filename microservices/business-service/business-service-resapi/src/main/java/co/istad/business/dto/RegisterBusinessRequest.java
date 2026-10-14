package co.istad.business.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.util.List;

public record RegisterBusinessRequest(
        @NotBlank(message = "Business name is required")
        String name,

        @NotBlank(message = "Email is required")
        @Email(message = "Email must be valid")
        String email,

        @NotBlank(message = "Phone number is required")
        String phone,

        String address,

        @Valid
        @NotNull(message = "Products list cannot be null")
        List<ProductRequest> products
) {
}
