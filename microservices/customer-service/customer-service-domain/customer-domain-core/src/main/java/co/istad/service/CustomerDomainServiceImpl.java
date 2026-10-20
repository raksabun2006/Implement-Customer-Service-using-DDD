package co.istad.service;

import co.istad.entity.Customer;
import co.istad.event.CustomerDeactivatedEvent;
import co.istad.event.CustomerInitiatedEvent;
import co.istad.event.CustomerSegmentAssignedEvent;
import co.istad.event.CustomerUpdatedEvent;
import co.istad.valueObject.CustomerSegment;

import java.time.ZoneId;
import java.time.ZonedDateTime;

public class CustomerDomainServiceImpl implements CustomerDomainService {

    @Override
    public CustomerInitiatedEvent initiateCustomer(Customer customer) {
        customer.initiateCustomer();
        return new CustomerInitiatedEvent(customer, ZonedDateTime.now(ZoneId.of("UTC")));
    }

    @Override
    public CustomerUpdatedEvent updateCustomer(Customer customer) {
        customer.updateCustomer(customer.getFamilyName(), customer.getGivenName(), customer.getSegment());
        return new CustomerUpdatedEvent(customer, ZonedDateTime.now(ZoneId.of("UTC")));
    }

    @Override
    public CustomerDeactivatedEvent deactivateCustomer(Customer customer) {
        customer.deactivateCustomer();
        return new CustomerDeactivatedEvent(customer, ZonedDateTime.now(ZoneId.of("UTC")));
    }

    @Override
    public CustomerSegmentAssignedEvent assignSegment(Customer customer, CustomerSegment segment) {
        customer.assignSegment(segment);
        return new CustomerSegmentAssignedEvent(customer, segment, ZonedDateTime.now(ZoneId.of("UTC")));
    }
}
