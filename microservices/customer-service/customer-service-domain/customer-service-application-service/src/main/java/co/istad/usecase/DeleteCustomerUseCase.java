package co.istad.usecase;

import co.istad.dto.DeleteCustomerCommand;
import reactor.core.publisher.Mono;

import java.util.UUID;

public interface DeleteCustomerUseCase {

    Mono<Void> execute(DeleteCustomerCommand command);

    default Mono<Void> execute(UUID customerId) {
        return execute(new DeleteCustomerCommand(customerId));
    }
}
