package co.istad.usecase;

import co.istad.dto.CustomerResult;
import co.istad.valueObject.CustomerSegment;
import reactor.core.publisher.Mono;

import java.util.UUID;

public interface AssignCustomerSegmentUseCase {

    Mono<CustomerResult> execute(UUID customerId, CustomerSegment segment);

    Mono<CustomerResult> evaluateAndAssign(UUID customerId, int completedOrders, double totalSpent);
}
