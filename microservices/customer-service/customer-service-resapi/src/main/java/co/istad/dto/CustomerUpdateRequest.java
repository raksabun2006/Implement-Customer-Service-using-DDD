package co.istad.dto;

import co.istad.valueObject.CustomerSegment;
import jakarta.validation.constraints.NotBlank;

public record CustomerUpdateRequest(
        @NotBlank(message = "Family name is required")
        String familyName,

        @NotBlank(message = "Given name is required")
        String givenName,

        CustomerSegment segment
) {
    public CustomerUpdateRequest(String familyName, String givenName) {
        this(familyName, givenName, null);
    }
}
