package co.istad.usecase;

import co.istad.dto.UpdateCustomerCommand;
import co.istad.dto.UpdateCustomerResult;
import reactor.core.publisher.Mono;

public interface UpdateCustomerUseCase {

    Mono<UpdateCustomerResult> execute(UpdateCustomerCommand command);

}
