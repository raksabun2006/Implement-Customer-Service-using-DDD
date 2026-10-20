package co.istad.usecase;

import co.istad.CustomerId;
import co.istad.dto.CustomerQueryFilter;
import co.istad.dto.CustomerResult;
import co.istad.dto.PageResult;
import co.istad.entity.Customer;
import co.istad.exception.CustomerNotFoundException;
import co.istad.port.out.CustomerRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.UUID;

@Slf4j
@Component
@RequiredArgsConstructor
public class GetCustomerUseCase {

    private final CustomerRepository customerRepository;

    public Mono<CustomerResult> findById(UUID customerId) {
        log.info("Fetching customer with id: {}", customerId);
        return customerRepository.findById(new CustomerId(customerId))
                .switchIfEmpty(Mono.error(new CustomerNotFoundException("Customer not found with id: " + customerId)))
                .map(this::toCustomerResult);
    }

    public Flux<CustomerResult> findAll() {
        log.info("Fetching all customers");
        return customerRepository.findAll()
                .map(this::toCustomerResult);
    }

    public Flux<CustomerResult> getAll() {
        return findAll();
    }

    public Mono<PageResult<CustomerResult>> findByFilter(CustomerQueryFilter filter) {
        log.info("Filtering customers with filter: {}", filter);

        Mono<java.util.List<CustomerResult>> contentMono = customerRepository.findByFilter(filter)
                .map(this::toCustomerResult)
                .collectList();

        Mono<Long> totalMono = customerRepository.countByFilter(filter);

        return Mono.zip(contentMono, totalMono)
                .map(tuple -> PageResult.of(tuple.getT1(), filter.page(), filter.size(), tuple.getT2()));
    }

    private CustomerResult toCustomerResult(Customer customer) {
        return new CustomerResult(
                customer.getId() != null ? customer.getId().value() : null,
                customer.getUsername(),
                customer.getFamilyName(),
                customer.getGivenName(),
                customer.getEmail() != null ? customer.getEmail().value() : null,
                customer.getPhoneNumber() != null ? customer.getPhoneNumber().value() : null,
                customer.getStatus(),
                customer.getSegment(),
                customer.getCreatedAt()
        );
    }
}
