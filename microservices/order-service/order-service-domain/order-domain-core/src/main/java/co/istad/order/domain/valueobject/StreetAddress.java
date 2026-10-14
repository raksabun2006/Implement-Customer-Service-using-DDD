package co.istad.order.domain.valueobject;

import java.util.Objects;
import java.util.UUID;

public record StreetAddress(
        UUID id,
        String street,
        String postalCode,
        String city
) {
    public StreetAddress {
        Objects.requireNonNull(street, "street must not be null");
        Objects.requireNonNull(postalCode, "postalCode must not be null");
        Objects.requireNonNull(city, "city must not be null");
    }
}
