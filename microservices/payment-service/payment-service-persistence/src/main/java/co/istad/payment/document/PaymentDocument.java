package co.istad.payment.document;

import co.istad.payment.domain.valueobject.PaymentStatus;
import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.math.BigDecimal;
import java.time.ZonedDateTime;
import java.util.UUID;

@Document(collection = "payments")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PaymentDocument {

    @Id
    private UUID id;

    private UUID customerId;

    private UUID orderId;

    private BigDecimal price;

    private PaymentStatus paymentStatus;

    private ZonedDateTime createdAt;
}
