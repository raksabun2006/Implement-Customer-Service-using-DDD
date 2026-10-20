package co.istad.usecase;

import co.istad.CustomerId;
import co.istad.dto.DeleteCustomerCommand;
import co.istad.exception.CustomerNotFoundException;
import co.istad.port.out.CustomerRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

@Slf4j
@Service
@RequiredArgsConstructor
public class DeleteCustomerUseCaseImpl implements DeleteCustomerUseCase {

    private final CustomerRepository customerRepository;

    @Override
    public Mono<Void> execute(DeleteCustomerCommand command) {
        log.info("Deleting customer with id: {}", command.customerId());

        return customerRepository.findById(new CustomerId(command.customerId()))
                .switchIfEmpty(Mono.error(new CustomerNotFoundException(
                        "Customer not found with id: " + command.customerId()
                )))
                .flatMap(customer -> customerRepository.deleteById(customer.getId()))
                .doOnSuccess(v -> log.info("Customer deleted successfully with id: {}", command.customerId()))
                .doOnError(error -> log.error("Failed to delete customer: {}", error.getMessage(), error));
    }
}
