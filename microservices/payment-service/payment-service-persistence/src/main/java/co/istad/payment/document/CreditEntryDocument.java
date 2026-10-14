package co.istad.payment.document;

import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.math.BigDecimal;
import java.util.UUID;

@Document(collection = "credit_entries")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CreditEntryDocument {

    @Id
    private UUID id;

    @Indexed(unique = true)
    private UUID customerId;

    private BigDecimal totalCreditAmount;
}
