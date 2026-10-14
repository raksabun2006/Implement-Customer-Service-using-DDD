package co.istad.mapper;

import co.istad.CustomerId;
import co.istad.document.CustomerDocument;
import co.istad.entity.Customer;
import org.springframework.stereotype.Component;

@Component
public class CustomerPersistenceMapper {

    public CustomerDocument toDocument(Customer customer) {
        if (customer == null) {
            return null;
        }

        return CustomerDocument.builder()
                .customerId(customer.getId() != null ? customer.getId().value() : null)
                .username(customer.getUsername())
                .familyName(customer.getFamilyName())
                .givenName(customer.getGivenName())
                .email(customer.getEmail())
                .phoneNumber(customer.getPhoneNumber())
                .status(customer.getStatus())
                .build();
    }

    public Customer toDomain(CustomerDocument document) {
        if (document == null) {
            return null;
        }

        return Customer.builder()
                .id(document.getCustomerId() != null ? new CustomerId(document.getCustomerId()) : null)
                .username(document.getUsername())
                .familyName(document.getFamilyName())
                .givenName(document.getGivenName())
                .email(document.getEmail())
                .phoneNumber(document.getPhoneNumber())
                .status(document.getStatus())
                .build();
    }
}
