package co.istad.adapter;

import co.istad.CustomerId;
import co.istad.entity.Customer;
import co.istad.entity.CustomerEntity;
import co.istad.mapper.CustomerPersistenceMapper;
import co.istad.port.out.CustomerRepository;
import co.istad.repository.CustomerJpaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Optional;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class CustomerRepositoryAdapter implements CustomerRepository {

    private final CustomerJpaRepository customerJpaRepository;
    private final CustomerPersistenceMapper customerPersistenceMapper;

    @Override
    public Customer save(Customer customer) {

        CustomerEntity entity =
                customerPersistenceMapper.toEntity(customer);

        CustomerEntity savedEntity =
                customerJpaRepository.save(entity);

        return customerPersistenceMapper.toDomain(savedEntity);
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
        return customerJpaRepository.findById(customerId)
                .map(customerPersistenceMapper::toDomain);
    }
}
