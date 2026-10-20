package co.istad.usecase;

import co.istad.dto.InitiateCustomerCommand;
import co.istad.dto.InitiateCustomerResult;
import co.istad.entity.Customer;
import co.istad.event.CustomerInitiatedEvent;
import co.istad.exception.CustomerAlreadyExistsException;
import co.istad.port.out.CustomerRepository;
import co.istad.service.CustomerDomainService;
import co.istad.service.CustomerSegmentationService;
import co.istad.valueObject.CustomerSegment;
import co.istad.valueObject.Email;
import co.istad.valueObject.PhoneNumber;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

@Component
@Slf4j
@RequiredArgsConstructor
public class CreateCustomerUseCase {

    private final CustomerRepository customerRepository;
    private final CustomerDomainService customerDomainService;
    private final CustomerSegmentationService customerSegmentationService;

    public Mono<InitiateCustomerResult> create(InitiateCustomerCommand command) {
        return execute(command);
    }

    public Mono<InitiateCustomerResult> execute(InitiateCustomerCommand command) {
        log.info("Executing create customer for username: {}", command.username());

        return customerRepository.existsByUsername(command.username())
                .flatMap(exists -> {
                    if (exists) {
                        log.warn("Customer with username '{}' already exists", command.username());
                        return Mono.error(new CustomerAlreadyExistsException(
                                "Customer with username '" + command.username() + "' already exists!"
                        ));
                    }

                    CustomerSegment initialSegment = command.segment() != null
                            ? command.segment()
                            : customerSegmentationService.getDefaultSegment();

                    return Mono.fromCallable(() -> {
                        Customer customer = Customer.builder()
                                .username(command.username())
                                .familyName(command.familyName())
                                .givenName(command.givenName())
                                .email(new Email(command.email()))
                                .phoneNumber(new PhoneNumber(command.phoneNumber()))
                                .segment(initialSegment)
                                .build();

                        CustomerInitiatedEvent customerInitiatedEvent = customerDomainService.initiateCustomer(customer);
                        log.info("Customer initiated event produced at: {}", customerInitiatedEvent.getInitiatedAt());
                        return customer;
                    }).flatMap(customerRepository::save);
                })
                .map(this::toInitiateCustomerResult)
                .doOnSuccess(result -> log.info("Customer created successfully with id: {}", result.customerId()))
                .doOnError(error -> log.error("Failed to create customer: {}", error.getMessage(), error));
    }

    private InitiateCustomerResult toInitiateCustomerResult(Customer customer) {
        return new InitiateCustomerResult(
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
