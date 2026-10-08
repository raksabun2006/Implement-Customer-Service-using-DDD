package co.istad.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

public record CustomerInitiateRequest(

        @NotBlank(message = "Username is required")
        String username,

        @NotBlank(message = "Family name is required")
        String familyName,

        @NotBlank(message = "Given name is required")
        String givenName,

        @NotBlank(message = "Email is required")
        @Email(message = "Email must be valid")
        String email,

        @NotBlank(message = "Phone number is required")
        @Pattern(
                regexp = "^\\+?[0-9]{8,15}$",
                message = "Phone number must be valid"
        )
        String phoneNumber
) {
}