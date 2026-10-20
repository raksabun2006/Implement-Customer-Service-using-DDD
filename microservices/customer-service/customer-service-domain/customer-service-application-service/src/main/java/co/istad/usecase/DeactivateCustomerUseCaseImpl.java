package co.istad.usecase;

import co.istad.CustomerId;
import co.istad.dto.DeactivateCustomerCommand;
import co.istad.entity.Customer;
import co.istad.event.CustomerDeactivatedEvent;
import co.istad.exception.CustomerNotFoundException;
import co.istad.port.out.CustomerRepository;
import co.istad.service.CustomerDomainService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

@Slf4j
@Service
@RequiredArgsConstructor
public class DeactivateCustomerUseCaseImpl implements DeactivateCustomerUseCase {

    private final CustomerRepository customerRepository;
    private final CustomerDomainService customerDomainService;

    @Override
    public Mono<Void> execute(DeactivateCustomerCommand command) {
        log.info("Deactivating customer with id: {}", command.customerId());

        return customerRepository.findById(new CustomerId(command.customerId()))
                .switchIfEmpty(Mono.error(new CustomerNotFoundException(
                        "Customer not found with id: " + command.customerId()
                )))
                .flatMap(customer -> Mono.fromCallable(() -> {
                    customer.deactivateCustomer();
                    CustomerDeactivatedEvent customerDeactivatedEvent = customerDomainService.deactivateCustomer(customer);
                    log.info("Customer deactivated event produced at: {}", customerDeactivatedEvent.getInitiatedAt());
                    return customer;
                }))
                .flatMap(customerRepository::save)
                .then();
    }
}
