package co.istad.business.domain.valueobject;

import java.util.Objects;

public record BusinessContact(
        String email,
        String phone,
        String address
) {
    public BusinessContact {
        Objects.requireNonNull(email, "email must not be null");
        Objects.requireNonNull(phone, "phone must not be null");
    }
}
