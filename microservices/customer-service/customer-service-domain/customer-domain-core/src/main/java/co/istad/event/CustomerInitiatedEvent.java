package co.istad.event;

import co.istad.entity.Customer;

import java.time.ZonedDateTime;

public class CustomerInitiatedEvent implements DomainEvent<Customer> {

    private final Customer customer;
    private final ZonedDateTime initiatedAt;

    public Customer getCustomer() {
        return customer;
    }

    public ZonedDateTime getInitiatedAt() {
        return initiatedAt;
    }

    public CustomerInitiatedEvent(Customer customer, ZonedDateTime initiatedAt) {
        this.customer = customer;
        this.initiatedAt = initiatedAt;
    }
}
