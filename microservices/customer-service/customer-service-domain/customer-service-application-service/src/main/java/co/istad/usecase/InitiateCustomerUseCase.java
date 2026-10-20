package co.istad.usecase;

import co.istad.dto.InitiateCustomerCommand;
import co.istad.dto.InitiateCustomerResult;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

@Component
@Slf4j
@RequiredArgsConstructor
public class InitiateCustomerUseCase {

    private final CreateCustomerUseCase createCustomerUseCase;

    public Mono<InitiateCustomerResult> execute(InitiateCustomerCommand command) {
        return createCustomerUseCase.execute(command);
    }
}
