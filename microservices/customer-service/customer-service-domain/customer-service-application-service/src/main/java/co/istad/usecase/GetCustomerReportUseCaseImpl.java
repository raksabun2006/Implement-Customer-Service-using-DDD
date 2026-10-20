package co.istad.usecase;

import co.istad.dto.CustomerReportResult;
import co.istad.port.out.CustomerRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

@Slf4j
@Service
@RequiredArgsConstructor
public class GetCustomerReportUseCaseImpl implements GetCustomerReportUseCase {

    private final CustomerRepository customerRepository;

    @Override
    public Mono<CustomerReportResult> execute() {
        log.info("Generating customer report with status and segment aggregation");
        return customerRepository.getCustomerReport()
                .doOnSuccess(r -> log.info("Successfully generated customer report for {} total customers", r.totalCustomers()))
                .doOnError(e -> log.error("Error generating customer report: {}", e.getMessage(), e));
    }
}
