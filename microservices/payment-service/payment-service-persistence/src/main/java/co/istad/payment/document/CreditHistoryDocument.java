package co.istad.payment.document;

import co.istad.payment.domain.valueobject.TransactionType;
import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.math.BigDecimal;
import java.util.UUID;

@Document(collection = "credit_histories")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CreditHistoryDocument {

    @Id
    private UUID id;

    private UUID customerId;

    private BigDecimal amount;

    private TransactionType transactionType;
}
