package co.istad.port.out;

import co.istad.CustomerId;
import co.istad.entity.Customer;

import java.util.Optional;
import java.util.UUID;

public interface CustomerRepository {

    Customer save(Customer customer);

    Optional<Customer> findById(CustomerId customerId);

    Optional<Customer> findById(UUID customerId);

}
