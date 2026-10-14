package co.istad.order.document;

import co.istad.order.domain.valueobject.OrderStatus;
import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Document(collection = "orders")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OrderDocument {

    @Id
    private UUID id;

    private UUID customerId;

    private UUID businessId;

    @Indexed(unique = true)
    private UUID trackingId;

    private BigDecimal price;

    private OrderStatus orderStatus;

    private List<String> failureMessages;

    private OrderAddressDocument address;

    @Builder.Default
    private List<OrderItemDocument> items = new ArrayList<>();
}
