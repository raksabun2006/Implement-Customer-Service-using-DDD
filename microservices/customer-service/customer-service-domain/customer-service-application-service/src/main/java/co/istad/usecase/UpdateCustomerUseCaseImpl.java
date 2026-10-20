package co.istad.usecase;

import co.istad.CustomerId;
import co.istad.dto.UpdateCustomerCommand;
import co.istad.dto.UpdateCustomerResult;
import co.istad.entity.Customer;
import co.istad.event.CustomerUpdatedEvent;
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
public class UpdateCustomerUseCaseImpl implements UpdateCustomerUseCase {

    private final CustomerRepository customerRepository;
    private final CustomerDomainService customerDomainService;

    @Override
    public Mono<UpdateCustomerResult> execute(UpdateCustomerCommand command) {
        log.info("Updating customer with id: {}", command.customerId());

        return customerRepository.findById(new CustomerId(command.customerId()))
                .switchIfEmpty(Mono.error(new CustomerNotFoundException(
                        "Customer not found with id: " + command.customerId()
                )))
                .flatMap(customer -> Mono.fromCallable(() -> {
                    customer.updateCustomer(command.familyName(), command.givenName(), command.segment());
                    CustomerUpdatedEvent customerUpdatedEvent = customerDomainService.updateCustomer(customer);
                    log.info("Customer updated event produced at: {}", customerUpdatedEvent.getInitiatedAt());
                    return customer;
                }))
                .flatMap(customerRepository::save)
                .map(this::toUpdateCustomerResult)
                .doOnSuccess(result -> log.info("Customer updated successfully with id: {}", result.customerId()))
                .doOnError(error -> log.error("Failed to update customer: {}", error.getMessage(), error));
    }

    private UpdateCustomerResult toUpdateCustomerResult(Customer customer) {
        return new UpdateCustomerResult(
                customer.getId().value(),
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
