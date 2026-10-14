package co.istad.adapter;

import co.istad.CustomerId;
import co.istad.document.CustomerDocument;
import co.istad.entity.Customer;
import co.istad.mapper.CustomerPersistenceMapper;
import co.istad.port.out.CustomerRepository;
import co.istad.repository.CustomerMongoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Optional;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class CustomerRepositoryAdapter implements CustomerRepository {

    private final CustomerMongoRepository customerMongoRepository;
    private final CustomerPersistenceMapper customerPersistenceMapper;

    @Override
    public Customer save(Customer customer) {
        CustomerDocument document = customerPersistenceMapper.toDocument(customer);
        CustomerDocument savedDocument = customerMongoRepository.save(document);
        return customerPersistenceMapper.toDomain(savedDocument);
    }

    @Override
    public Optional<Customer> findById(CustomerId customerId) {
        if (customerId == null || customerId.value() == null) {
            return Optional.empty();
        }
        return findById(customerId.value());
    }

    @Override
    public Optional<Customer> findById(UUID customerId) {
        if (customerId == null) {
            return Optional.empty();
        }
        return customerMongoRepository.findById(customerId)
                .map(customerPersistenceMapper::toDomain);
    }
}
