package co.istad.business.document;

import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Document(collection = "businesses")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BusinessDocument {

    @Id
    private UUID id;
    private String name;
    private BusinessContactDocument contact;
    private boolean active;
    @Builder.Default
    private List<ProductDocument> products = new ArrayList<>();
}
