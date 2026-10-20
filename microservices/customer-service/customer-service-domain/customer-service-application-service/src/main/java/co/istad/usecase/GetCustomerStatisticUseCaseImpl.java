package co.istad.usecase;

import co.istad.dto.CustomerStatisticResult;
import co.istad.port.out.CustomerRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

@Slf4j
@Service
@RequiredArgsConstructor
public class GetCustomerStatisticUseCaseImpl implements GetCustomerStatisticUseCase {

    private final CustomerRepository customerRepository;

    @Override
    public Mono<CustomerStatisticResult> execute() {
        log.info("Generating customer statistics");
        return customerRepository.getCustomerStatistic()
                .doOnSuccess(s -> log.info("Successfully generated customer statistics for {} total customers", s.totalCustomers()))
                .doOnError(e -> log.error("Error generating customer statistics: {}", e.getMessage(), e));
    }
}
