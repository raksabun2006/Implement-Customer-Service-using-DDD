package co.istad.document;

import co.istad.valueObject.CustomerStatus;
import co.istad.valueObject.Email;
import co.istad.valueObject.PhoneNumber;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.annotation.*;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.DocumentReference;

import java.time.Instant;
import java.util.UUID;

@Document(collection = "customerDocument")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CustomerDocument {

    @Id
    private UUID customerId;

    @Indexed(unique = true)
    private String username;

    private String familyName;

    private String givenName;

    private Email email;

    private PhoneNumber phoneNumber;

    private CustomerStatus status;

    @DocumentReference
    private CustomerSegmentDocument segment;

    @CreatedBy
    private Instant createBy;

    @CreatedDate
    private Instant createdAt;

    @LastModifiedBy
    private Instant modifiedBy;

    @LastModifiedDate
    private Instant modifiedAt;
}
