package co.istad;

import co.istad.entity.Customer;
import co.istad.exception.CustomerDomainException;
import co.istad.valueObject.CustomerSegment;
import co.istad.valueObject.CustomerStatus;
import co.istad.valueObject.Email;
import co.istad.valueObject.PhoneNumber;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class CustomerTest {

    @Test
    @DisplayName("Should successfully initiate customer with ACTIVE status and SILVER segment")
    void shouldInitiateCustomerSuccessfully() {
        Customer customer = Customer.builder()
                .username("john_doe")
                .familyName("Doe")
                .givenName("John")
                .email(new Email("john@example.com"))
                .phoneNumber(new PhoneNumber("+85512345678"))
                .build();

        customer.initiateCustomer();

        assertThat(customer.getId()).isNotNull();
        assertThat(customer.getId().value()).isNotNull();
        assertThat(customer.getStatus()).isEqualTo(CustomerStatus.ACTIVE);
        assertThat(customer.getSegment()).isEqualTo(CustomerSegment.SILVER);
        assertThat(customer.getCreatedAt()).isNotNull();
    }

    @Test
    @DisplayName("Should preserve specified segment on initiation if provided")
    void shouldPreserveSpecifiedSegmentOnInitiation() {
        Customer customer = Customer.builder()
                .username("gold_user")
                .familyName("Smith")
                .givenName("Jane")
                .email(new Email("jane@example.com"))
                .phoneNumber(new PhoneNumber("+85587654321"))
                .segment(CustomerSegment.GOLD)
                .build();

        customer.initiateCustomer();

        assertThat(customer.getStatus()).isEqualTo(CustomerStatus.ACTIVE);
        assertThat(customer.getSegment()).isEqualTo(CustomerSegment.GOLD);
    }

    @Test
    @DisplayName("Should throw exception when initiating customer that already has an id")
    void shouldThrowExceptionWhenInitiatingCustomerWithId() {
        Customer customer = Customer.builder()
                .id(new CustomerId(java.util.UUID.randomUUID()))
                .username("user")
                .familyName("Doe")
                .givenName("John")
                .email(new Email("user@example.com"))
                .phoneNumber(new PhoneNumber("+85512345678"))
                .build();

        assertThatThrownBy(customer::initiateCustomer)
                .isInstanceOf(CustomerDomainException.class)
                .hasMessageContaining("Customer id must be null");
    }

    @Test
    @DisplayName("Should update customer names and segment")
    void shouldUpdateCustomerNamesAndSegment() {
        Customer customer = Customer.builder()
                .username("john_doe")
                .familyName("Doe")
                .givenName("John")
                .email(new Email("john@example.com"))
                .phoneNumber(new PhoneNumber("+85512345678"))
                .build();
        customer.initiateCustomer();

        customer.updateCustomer("Smith", "Johnny", CustomerSegment.GOLD);

        assertThat(customer.getFamilyName()).isEqualTo("Smith");
        assertThat(customer.getGivenName()).isEqualTo("Johnny");
        assertThat(customer.getSegment()).isEqualTo(CustomerSegment.GOLD);
    }

    @Test
    @DisplayName("Should throw exception when updating with null names")
    void shouldThrowExceptionWhenUpdatingWithNullNames() {
        Customer customer = Customer.builder()
                .username("john_doe")
                .familyName("Doe")
                .givenName("John")
                .email(new Email("john@example.com"))
                .phoneNumber(new PhoneNumber("+85512345678"))
                .build();
        customer.initiateCustomer();

        assertThatThrownBy(() -> customer.updateCustomer(null, "Johnny"))
                .isInstanceOf(CustomerDomainException.class)
                .hasMessageContaining("familyName and givenName must not be null");
    }

    @Test
    @DisplayName("Should deactivate active customer and prevent deactivating inactive customer")
    void shouldHandleCustomerDeactivation() {
        Customer customer = Customer.builder()
                .username("john_doe")
                .familyName("Doe")
                .givenName("John")
                .email(new Email("john@example.com"))
                .phoneNumber(new PhoneNumber("+85512345678"))
                .build();
        customer.initiateCustomer();

        customer.deactivateCustomer();
        assertThat(customer.getStatus()).isEqualTo(CustomerStatus.INACTIVE);

        assertThatThrownBy(customer::deactivateCustomer)
                .isInstanceOf(CustomerDomainException.class)
                .hasMessageContaining("Could not deactivate");
    }

    @Test
    @DisplayName("Should assign segment directly")
    void shouldAssignSegmentDirectly() {
        Customer customer = Customer.builder()
                .username("vip_user")
                .familyName("Taylor")
                .givenName("Alex")
                .email(new Email("alex@example.com"))
                .phoneNumber(new PhoneNumber("+85599887766"))
                .build();
        customer.initiateCustomer();

        customer.assignSegment(CustomerSegment.PREMIUM);
        assertThat(customer.getSegment()).isEqualTo(CustomerSegment.PREMIUM);

        assertThatThrownBy(() -> customer.assignSegment(null))
                .isInstanceOf(CustomerDomainException.class)
                .hasMessageContaining("Customer segment cannot be null");
    }
}
