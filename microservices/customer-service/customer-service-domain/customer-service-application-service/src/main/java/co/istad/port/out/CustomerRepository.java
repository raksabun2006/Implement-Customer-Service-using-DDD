package co.istad.port.out;

import co.istad.CustomerId;
import co.istad.dto.CustomerQueryFilter;
import co.istad.dto.CustomerReportResult;
import co.istad.dto.CustomerStatisticResult;
import co.istad.entity.Customer;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.UUID;

public interface CustomerRepository {

    Mono<Customer> save(Customer customer);

    Mono<Customer> findById(CustomerId customerId);

    Mono<Customer> findById(UUID customerId);

    Flux<Customer> findAll();

    Mono<Boolean> existsByUsername(String username);

    Mono<Customer> findByUsername(String username);

    Mono<Void> deleteById(CustomerId customerId);

    Mono<Void> deleteById(UUID customerId);

    Flux<Customer> findByFilter(CustomerQueryFilter filter);

    Mono<Long> countByFilter(CustomerQueryFilter filter);

    Mono<CustomerReportResult> getCustomerReport();

    Mono<CustomerStatisticResult> getCustomerStatistic();
}
