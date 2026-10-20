package co.istad.event;

import co.istad.entity.Customer;
import co.istad.valueObject.CustomerSegment;

import java.time.ZonedDateTime;

public class CustomerSegmentAssignedEvent implements DomainEvent<Customer> {

    private final Customer customer;
    private final CustomerSegment segment;
    private final ZonedDateTime assignedAt;

    public CustomerSegmentAssignedEvent(Customer customer, CustomerSegment segment, ZonedDateTime assignedAt) {
        this.customer = customer;
        this.segment = segment;
        this.assignedAt = assignedAt;
    }

    public Customer getCustomer() {
        return customer;
    }

    public CustomerSegment getSegment() {
        return segment;
    }

    public ZonedDateTime getAssignedAt() {
        return assignedAt;
    }
}
