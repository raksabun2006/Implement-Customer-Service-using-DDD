package co.istad.usecase;

import co.istad.CustomerId;
import co.istad.dto.UpdateCustomerCommand;
import co.istad.dto.UpdateCustomerResult;
import co.istad.entity.Customer;
import co.istad.event.CustomerUpdatedEvent;
import co.istad.exception.CustomerDomainException;
import co.istad.port.out.CustomerRepository;
import co.istad.service.CustomerDomainService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class UpdateCustomerUseCaseImpl implements UpdateCustomerUseCase {

    private final CustomerRepository customerRepository;
    private final CustomerDomainService customerDomainService;

    @Override
    public UpdateCustomerResult execute(UpdateCustomerCommand command) {
        log.info("Updating customer with id: {}", command.customerId());

        Customer customer = customerRepository.findById(new CustomerId(command.customerId()))
                .orElseThrow(() -> new CustomerDomainException(
                        "Customer not found with id: " + command.customerId()
                ));

        customer.updateCustomer(command.familyName(), command.givenName());
        CustomerUpdatedEvent customerUpdatedEvent = customerDomainService.updateCustomer(customer);
        log.info("Customer updated event produced at: {}", customerUpdatedEvent.getInitiatedAt());

        Customer savedCustomer = customerRepository.save(customer);

        return new UpdateCustomerResult(
                savedCustomer.getId().value(),
                savedCustomer.getUsername(),
                savedCustomer.getFamilyName(),
                savedCustomer.getGivenName(),
                savedCustomer.getEmail() != null ? savedCustomer.getEmail().value() : null,
                savedCustomer.getPhoneNumber() != null ? savedCustomer.getPhoneNumber().value() : null
        );
    }
}
