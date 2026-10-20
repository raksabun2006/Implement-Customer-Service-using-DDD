package co.istad.usecase;

import co.istad.dto.DeactivateCustomerCommand;
import reactor.core.publisher.Mono;

public interface DeactivateCustomerUseCase {

    Mono<Void> execute(DeactivateCustomerCommand command);

}
