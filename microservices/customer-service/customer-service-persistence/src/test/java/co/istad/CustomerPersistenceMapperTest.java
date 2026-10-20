package co.istad;

import co.istad.document.CustomerDocument;
import co.istad.document.CustomerSegmentDocument;
import co.istad.entity.Customer;
import co.istad.mapper.CustomerPersistenceMapper;
import co.istad.valueObject.CustomerSegment;
import co.istad.valueObject.CustomerStatus;
import co.istad.valueObject.Email;
import co.istad.valueObject.PhoneNumber;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

class CustomerPersistenceMapperTest {

    private final CustomerPersistenceMapper mapper = new CustomerPersistenceMapper();

    @Test
    @DisplayName("Should map domain Customer to CustomerDocument with segment reference and createdAt")
    void shouldMapCustomerToDocument() {
        UUID id = UUID.randomUUID();
        Instant now = Instant.now();
        Customer customer = Customer.builder()
                .id(new CustomerId(id))
                .username("sarah_c")
                .familyName("Connor")
                .givenName("Sarah")
                .email(new Email("sarah@resistance.com"))
                .phoneNumber(new PhoneNumber("+12345678901"))
                .status(CustomerStatus.ACTIVE)
                .segment(CustomerSegment.PREMIUM)
                .createdAt(now)
                .build();

        CustomerDocument document = mapper.toDocument(customer);

        assertThat(document).isNotNull();
        assertThat(document.getCustomerId()).isEqualTo(id);
        assertThat(document.getUsername()).isEqualTo("sarah_c");
        assertThat(document.getFamilyName()).isEqualTo("Connor");
        assertThat(document.getGivenName()).isEqualTo("Sarah");
        assertThat(document.getStatus()).isEqualTo(CustomerStatus.ACTIVE);
        assertThat(document.getSegment()).isNotNull();
        assertThat(document.getSegment().getId()).isEqualTo("PREMIUM");
        assertThat(document.getCreatedAt()).isEqualTo(now);
    }

    @Test
    @DisplayName("Should map CustomerDocument to domain Customer with segment reference and createdAt")
    void shouldMapDocumentToCustomer() {
        UUID id = UUID.randomUUID();
        Instant now = Instant.now();
        CustomerDocument document = CustomerDocument.builder()
                .customerId(id)
                .username("john_c")
                .familyName("Connor")
                .givenName("John")
                .email(new Email("john@resistance.com"))
                .phoneNumber(new PhoneNumber("+12345678902"))
                .status(CustomerStatus.ACTIVE)
                .segment(CustomerSegmentDocument.builder()
                        .id("GOLD")
                        .name("GOLD")
                        .description("Gold tier")
                        .build())
                .createdAt(now)
                .build();

        Customer customer = mapper.toDomain(document);

        assertThat(customer).isNotNull();
        assertThat(customer.getId().value()).isEqualTo(id);
        assertThat(customer.getUsername()).isEqualTo("john_c");
        assertThat(customer.getSegment()).isEqualTo(CustomerSegment.GOLD);
        assertThat(customer.getCreatedAt()).isEqualTo(now);
    }
}
