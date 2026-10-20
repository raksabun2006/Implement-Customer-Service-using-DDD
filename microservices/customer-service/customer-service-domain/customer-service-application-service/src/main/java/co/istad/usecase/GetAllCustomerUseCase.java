package co.istad.usecase;

import co.istad.dto.CustomerResult;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;

@Component
@RequiredArgsConstructor
public class GetAllCustomerUseCase {

    private final GetCustomerUseCase getCustomerUseCase;

    public Flux<CustomerResult> execute() {
        return getCustomerUseCase.findAll();
    }
}
