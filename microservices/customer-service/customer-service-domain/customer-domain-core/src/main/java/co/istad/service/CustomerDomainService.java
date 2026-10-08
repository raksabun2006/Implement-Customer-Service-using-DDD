package co.istad.service;

import co.istad.entity.Customer;
import co.istad.event.CustomerDeactivatedEvent;
import co.istad.event.CustomerInitiatedEvent;
import co.istad.event.CustomerUpdatedEvent;

public interface CustomerDomainService {

    CustomerInitiatedEvent initiateCustomer(Customer customer);

    CustomerUpdatedEvent updateCustomer(Customer customer);

    CustomerDeactivatedEvent deactivateCustomer(Customer customer);
}
