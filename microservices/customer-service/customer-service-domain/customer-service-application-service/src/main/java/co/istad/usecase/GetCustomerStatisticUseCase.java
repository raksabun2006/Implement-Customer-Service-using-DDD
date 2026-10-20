package co.istad.usecase;

import co.istad.dto.CustomerStatisticResult;
import reactor.core.publisher.Mono;

public interface GetCustomerStatisticUseCase {

    Mono<CustomerStatisticResult> execute();

}
