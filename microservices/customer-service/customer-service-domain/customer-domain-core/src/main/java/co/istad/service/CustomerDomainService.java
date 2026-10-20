package co.istad.service;

import co.istad.entity.Customer;
import co.istad.event.CustomerDeactivatedEvent;
import co.istad.event.CustomerInitiatedEvent;
import co.istad.event.CustomerSegmentAssignedEvent;
import co.istad.event.CustomerUpdatedEvent;
import co.istad.valueObject.CustomerSegment;

public interface CustomerDomainService {

    CustomerInitiatedEvent initiateCustomer(Customer customer);

    CustomerUpdatedEvent updateCustomer(Customer customer);

    CustomerDeactivatedEvent deactivateCustomer(Customer customer);

    CustomerSegmentAssignedEvent assignSegment(Customer customer, CustomerSegment segment);
}
