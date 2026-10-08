package co.istad.usecase;

import co.istad.CustomerId;
import co.istad.dto.DeactivateCustomerCommand;
import co.istad.entity.Customer;
import co.istad.event.CustomerDeactivatedEvent;
import co.istad.exception.CustomerDomainException;
import co.istad.port.out.CustomerRepository;
import co.istad.service.CustomerDomainService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class DeactivateCustomerUseCaseImpl implements DeactivateCustomerUseCase {

    private final CustomerRepository customerRepository;
    private final CustomerDomainService customerDomainService;

    @Override
    public void execute(DeactivateCustomerCommand command) {
        log.info("Deactivating customer with id: {}", command.customerId());

        Customer customer = customerRepository.findById(new CustomerId(command.customerId()))
                .orElseThrow(() -> new CustomerDomainException(
                        "Customer not found with id: " + command.customerId()
                ));

        CustomerDeactivatedEvent customerDeactivatedEvent = customerDomainService.deactivateCustomer(customer);
        log.info("Customer deactivated event produced at: {}", customerDeactivatedEvent.getInitiatedAt());

        customerRepository.save(customer);
    }
}
