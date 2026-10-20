package co.istad.usecase;

import co.istad.dto.CustomerReportResult;
import reactor.core.publisher.Mono;

public interface GetCustomerReportUseCase {

    Mono<CustomerReportResult> execute();

}
