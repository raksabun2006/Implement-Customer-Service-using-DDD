package co.istad.service;

import co.istad.entity.Customer;
import co.istad.event.CustomerDeactivatedEvent;
import co.istad.event.CustomerInitiatedEvent;
import co.istad.event.CustomerUpdatedEvent;

import java.time.ZoneId;
import java.time.ZonedDateTime;

public class CustomerDomainServiceImpl implements CustomerDomainService{

    @Override
    public CustomerInitiatedEvent initiateCustomer(Customer customer) {
        customer.initiateCustomer();
        return new CustomerInitiatedEvent(customer, ZonedDateTime.now(ZoneId.of("UTC")));
    }

    @Override
    public CustomerUpdatedEvent updateCustomer(Customer customer) {
        customer.updateCustomer(customer.getFamilyName(), customer.getGivenName());
        return new CustomerUpdatedEvent(customer, ZonedDateTime.now(ZoneId.of("UTC")));
    }

    @Override
    public CustomerDeactivatedEvent deactivateCustomer(Customer customer) {
        customer.deactivateCustomer();
        return new CustomerDeactivatedEvent(customer, ZonedDateTime.now(ZoneId.of("UTC")));
    }

}