package co.istad.order.document;

import lombok.*;

import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OrderAddressDocument {
    private UUID id;
    private String street;
    private String postalCode;
    private String city;
}
