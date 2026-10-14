package co.istad.business.document;

import lombok.*;

import java.math.BigDecimal;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProductDocument {
    private UUID id;
    private String name;
    private BigDecimal price;
    private int quantity;
    private boolean available;
}
