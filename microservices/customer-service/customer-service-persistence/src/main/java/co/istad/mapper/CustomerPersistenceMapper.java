package co.istad.mapper;

import co.istad.CustomerId;
import co.istad.document.CustomerDocument;
import co.istad.document.CustomerSegmentDocument;
import co.istad.entity.Customer;
import co.istad.valueObject.CustomerSegment;
import org.springframework.stereotype.Component;

import java.time.Instant;

@Component
public class CustomerPersistenceMapper {

    public CustomerDocument toDocument(Customer customer) {
        if (customer == null) {
            return null;
        }

        CustomerSegment segment = customer.getSegment();
        CustomerSegmentDocument segmentDoc = null;
        if (segment != null) {
            segmentDoc = CustomerSegmentDocument.builder()
                    .id(segment.name())
                    .name(segment.name())
                    .description(segment.name() + " tier customer segment")
                    .build();
        }

        return CustomerDocument.builder()
                .customerId(customer.getId() != null ? customer.getId().value() : null)
                .username(customer.getUsername())
                .familyName(customer.getFamilyName())
                .givenName(customer.getGivenName())
                .email(customer.getEmail())
                .phoneNumber(customer.getPhoneNumber())
                .status(customer.getStatus())
                .segment(segmentDoc)
                .createdAt(customer.getCreatedAt() != null ? customer.getCreatedAt() : Instant.now())
                .build();
    }

    public Customer toDomain(CustomerDocument document) {
        if (document == null) {
            return null;
        }

        CustomerSegment segment = CustomerSegment.SILVER;
        if (document.getSegment() != null && document.getSegment().getId() != null) {
            try {
                segment = CustomerSegment.valueOf(document.getSegment().getId().toUpperCase());
            } catch (IllegalArgumentException ignored) {
            }
        }

        return Customer.builder()
                .id(document.getCustomerId() != null ? new CustomerId(document.getCustomerId()) : null)
                .username(document.getUsername())
                .familyName(document.getFamilyName())
                .givenName(document.getGivenName())
                .email(document.getEmail())
                .phoneNumber(document.getPhoneNumber())
                .status(document.getStatus())
                .segment(segment)
                .createdAt(document.getCreatedAt())
                .build();
    }
}
