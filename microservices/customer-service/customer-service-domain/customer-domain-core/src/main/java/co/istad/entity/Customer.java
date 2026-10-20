package co.istad.entity;

import co.istad.CustomerId;
import co.istad.exception.CustomerDomainException;
import co.istad.valueObject.CustomerSegment;
import co.istad.valueObject.CustomerStatus;
import co.istad.valueObject.Email;
import co.istad.valueObject.PhoneNumber;

import java.time.Instant;
import java.util.UUID;

public class Customer extends AggregateRoot<CustomerId> {
    private final String username;
    private String familyName;
    private String givenName;
    private final Email email;
    private final PhoneNumber phoneNumber;
    private CustomerStatus status;
    private CustomerSegment segment;
    private Instant createdAt;

    // Domain Critical Logic
    public void initiateCustomer() {
        validateCustomer();
        super.setId(new CustomerId(UUID.randomUUID()));
        this.status = CustomerStatus.ACTIVE;
        if (this.segment == null) {
            this.segment = CustomerSegment.SILVER;
        }
        if (this.createdAt == null) {
            this.createdAt = Instant.now();
        }
    }

    private void validateCustomer() {
        if (super.getId() != null) {
            throw new CustomerDomainException("Customer id must be null");
        }
        if (status != null) {
            throw new CustomerDomainException("Customer status must be null");
        }
    }

    public void updateCustomer(String familyName, String givenName) {
        if (familyName == null || givenName == null) {
            throw new CustomerDomainException("familyName and givenName must not be null");
        }
        this.familyName = familyName;
        this.givenName = givenName;
    }

    public void updateCustomer(String familyName, String givenName, CustomerSegment segment) {
        updateCustomer(familyName, givenName);
        if (segment != null) {
            this.segment = segment;
        }
    }

    public void assignSegment(CustomerSegment segment) {
        if (segment == null) {
            throw new CustomerDomainException("Customer segment cannot be null");
        }
        this.segment = segment;
    }

    public void deactivateCustomer() {
        if (status != CustomerStatus.ACTIVE) {
            throw new CustomerDomainException("Could not deactivate because customer status is not active");
        }
        status = CustomerStatus.INACTIVE;
    }

    public void activateCustomer() {
        if (status == CustomerStatus.ACTIVE) {
            throw new CustomerDomainException("Customer is already active");
        }
        status = CustomerStatus.ACTIVE;
    }

    public String getUsername() {
        return username;
    }

    public String getFamilyName() {
        return familyName;
    }

    public String getGivenName() {
        return givenName;
    }

    public Email getEmail() {
        return email;
    }

    public PhoneNumber getPhoneNumber() {
        return phoneNumber;
    }

    public CustomerStatus getStatus() {
        return status;
    }

    public CustomerSegment getSegment() {
        return segment;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    private Customer(Builder builder) {
        super.setId(builder.id);
        username = builder.username;
        familyName = builder.familyName;
        givenName = builder.givenName;
        email = builder.email;
        phoneNumber = builder.phoneNumber;
        status = builder.status;
        segment = builder.segment;
        createdAt = builder.createdAt;
    }

    public static Builder builder() {
        return new Builder();
    }

    public static final class Builder {
        private CustomerId id;
        private String username;
        private String familyName;
        private String givenName;
        private Email email;
        private PhoneNumber phoneNumber;
        private CustomerStatus status;
        private CustomerSegment segment;
        private Instant createdAt;

        private Builder() {
        }

        public Builder id(CustomerId val) {
            id = val;
            return this;
        }

        public Builder username(String val) {
            username = val;
            return this;
        }

        public Builder familyName(String val) {
            familyName = val;
            return this;
        }

        public Builder givenName(String val) {
            givenName = val;
            return this;
        }

        public Builder email(Email val) {
            email = val;
            return this;
        }

        public Builder phoneNumber(PhoneNumber val) {
            phoneNumber = val;
            return this;
        }

        public Builder status(CustomerStatus val) {
            status = val;
            return this;
        }

        public Builder segment(CustomerSegment val) {
            segment = val;
            return this;
        }

        public Builder createdAt(Instant val) {
            createdAt = val;
            return this;
        }

        public Customer build() {
            return new Customer(this);
        }
    }
}
