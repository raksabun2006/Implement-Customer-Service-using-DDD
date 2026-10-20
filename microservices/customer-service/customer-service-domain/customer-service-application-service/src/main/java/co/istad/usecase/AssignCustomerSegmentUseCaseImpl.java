package co.istad.usecase;

import co.istad.CustomerId;
import co.istad.dto.CustomerResult;
import co.istad.entity.Customer;
import co.istad.event.CustomerSegmentAssignedEvent;
import co.istad.exception.CustomerNotFoundException;
import co.istad.port.out.CustomerRepository;
import co.istad.service.CustomerDomainService;
import co.istad.service.CustomerSegmentationService;
import co.istad.valueObject.CustomerSegment;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class AssignCustomerSegmentUseCaseImpl implements AssignCustomerSegmentUseCase {

    private final CustomerRepository customerRepository;
    private final CustomerDomainService customerDomainService;
    private final CustomerSegmentationService customerSegmentationService;

    @Override
    public Mono<CustomerResult> execute(UUID customerId, CustomerSegment segment) {
        log.info("Assigning segment '{}' to customer: {}", segment, customerId);

        return customerRepository.findById(new CustomerId(customerId))
                .switchIfEmpty(Mono.error(new CustomerNotFoundException("Customer not found with id: " + customerId)))
                .flatMap(customer -> Mono.fromCallable(() -> {
                    CustomerSegmentAssignedEvent event = customerDomainService.assignSegment(customer, segment);
                    log.info("Customer segment assigned event at: {}", event.getAssignedAt());
                    return customer;
                }))
                .flatMap(customerRepository::save)
                .map(this::toCustomerResult)
                .doOnSuccess(res -> log.info("Successfully updated customer segment to: {}", res.segment()))
                .doOnError(err -> log.error("Failed to update customer segment: {}", err.getMessage()));
    }

    @Override
    public Mono<CustomerResult> evaluateAndAssign(UUID customerId, int completedOrders, double totalSpent) {
        log.info("Evaluating segment for customer {} with orders: {}, spent: {}", customerId, completedOrders, totalSpent);
        CustomerSegment evaluatedSegment = customerSegmentationService.evaluateSegment(completedOrders, totalSpent);
        return execute(customerId, evaluatedSegment);
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
