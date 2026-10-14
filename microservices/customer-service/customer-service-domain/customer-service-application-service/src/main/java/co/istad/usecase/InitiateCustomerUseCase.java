package co.istad.usecase;

import co.istad.dto.InitiateCustomerCommand;
import co.istad.dto.InitiateCustomerResult;
import co.istad.entity.Customer;
import co.istad.event.CustomerInitiatedEvent;
import co.istad.port.out.CustomerRepository;
import co.istad.service.CustomerDomainService;
import co.istad.valueObject.Email;
import co.istad.valueObject.PhoneNumber;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Component
@Slf4j
@RequiredArgsConstructor
public class InitiateCustomerUseCase {

    private final CustomerRepository customerRepository;
    private final CustomerDomainService customerDomainService;

    public InitiateCustomerResult execute(InitiateCustomerCommand command) {
        log.info("initiate customer usecase: {}", command);

        Customer customer = Customer.builder()
                .username(command.username())
                .familyName(command.familyName())
                .givenName(command.givenName())
                .email(new Email(command.email()))
                .phoneNumber(new PhoneNumber(command.phoneNumber()))
                .build();

        CustomerInitiatedEvent customerInitiatedEvent = customerDomainService.initiateCustomer(customer);
        log.info("Customer initiated event produced at: {}", customerInitiatedEvent.getInitiatedAt());

        Customer savedCustomer = customerRepository.save(customer);

        return new InitiateCustomerResult(
                savedCustomer.getId().value(),
                savedCustomer.getUsername(),
                savedCustomer.getFamilyName(),
                savedCustomer.getGivenName(),
                savedCustomer.getEmail() != null ? savedCustomer.getEmail().value() : null,
                savedCustomer.getPhoneNumber() != null ? savedCustomer.getPhoneNumber().value() : null
        );
    }

}
