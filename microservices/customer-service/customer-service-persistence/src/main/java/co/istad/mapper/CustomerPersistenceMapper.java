package co.istad.mapper;

import co.istad.CustomerId;
import co.istad.entity.Customer;
import co.istad.entity.CustomerEntity;
import org.springframework.stereotype.Component;

@Component
public class CustomerPersistenceMapper {

    public CustomerEntity toEntity(Customer customer) {
        if (customer == null) {
            return null;
        }

        CustomerEntity entity = new CustomerEntity();

        if (customer.getId() != null) {
            entity.setCustomerId(customer.getId().value());
        }
        entity.setUsername(customer.getUsername());
        entity.setFamilyName(customer.getFamilyName());
        entity.setGivenName(customer.getGivenName());
        entity.setEmail(customer.getEmail());
        entity.setPhoneNumber(customer.getPhoneNumber());
        entity.setStatus(customer.getStatus());

        return entity;
    }

    public Customer toDomain(CustomerEntity entity) {
        if (entity == null) {
            return null;
        }

        return Customer.builder()
                .id(entity.getCustomerId() != null ? new CustomerId(entity.getCustomerId()) : null)
                .username(entity.getUsername())
                .familyName(entity.getFamilyName())
                .givenName(entity.getGivenName())
                .email(entity.getEmail())
                .phoneNumber(entity.getPhoneNumber())
                .status(entity.getStatus())
                .build();
    }
}
