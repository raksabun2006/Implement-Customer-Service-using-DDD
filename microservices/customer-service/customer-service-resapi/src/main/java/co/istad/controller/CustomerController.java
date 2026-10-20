package co.istad.controller;

import co.istad.dto.*;
import co.istad.mapper.CustomerWebMapper;
import co.istad.usecase.*;
import co.istad.valueObject.CustomerSegment;
import co.istad.valueObject.CustomerStatus;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.Instant;
import java.util.UUID;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/customers")
public class CustomerController {

    private final CreateCustomerUseCase createCustomerUseCase;
    private final UpdateCustomerUseCase updateCustomerUseCase;
    private final DeactivateCustomerUseCase deactivateCustomerUseCase;
    private final DeleteCustomerUseCase deleteCustomerUseCase;
    private final GetCustomerUseCase getCustomerUseCase;
    private final AssignCustomerSegmentUseCase assignCustomerSegmentUseCase;
    private final GetCustomerReportUseCase getCustomerReportUseCase;
    private final GetCustomerStatisticUseCase getCustomerStatisticUseCase;
    private final CustomerWebMapper customerWebMapper;

    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping
    public Mono<CustomerInitiateResponse> initiateCustomer(
            @Valid @RequestBody CustomerInitiateRequest customerInitiateRequest
    ) {
        return createCustomerUseCase.execute(
                customerWebMapper.toCommand(customerInitiateRequest)
        ).map(customerWebMapper::toResponse);
    }

    @GetMapping
    public Mono<PageResponse<CustomerResponse>> getCustomers(
            @RequestParam(required = false) String search,
            @RequestParam(required = false) CustomerStatus status,
            @RequestParam(required = false) CustomerSegment segment,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) Instant fromDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) Instant toDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) Instant startDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) Instant endDate,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "createdAt") String sortBy,
            @RequestParam(defaultValue = "desc") String sortDirection
    ) {
        Instant effectiveFrom = fromDate != null ? fromDate : startDate;
        Instant effectiveTo = toDate != null ? toDate : endDate;

        CustomerQueryFilter filter = CustomerQueryFilter.of(
                search,
                status,
                segment,
                effectiveFrom,
                effectiveTo,
                page,
                size,
                sortBy,
                sortDirection
        );

        return getCustomerUseCase.findByFilter(filter)
                .map(pageResult -> new PageResponse<>(
                        pageResult.content().stream().map(customerWebMapper::toResponse).toList(),
                        pageResult.page(),
                        pageResult.size(),
                        pageResult.totalElements(),
                        pageResult.totalPages(),
                        pageResult.first(),
                        pageResult.last()
                ));
    }

    @GetMapping("/all")
    public Flux<CustomerResponse> getAllCustomers() {
        return getCustomerUseCase.findAll()
                .map(customerWebMapper::toResponse);
    }

    @GetMapping("/{customerId}")
    public Mono<CustomerResponse> getCustomerById(@PathVariable UUID customerId) {
        return getCustomerUseCase.findById(customerId)
                .map(customerWebMapper::toResponse);
    }

    @PutMapping("/{customerId}")
    public Mono<CustomerUpdateResponse> updateCustomer(
            @PathVariable UUID customerId,
            @Valid @RequestBody CustomerUpdateRequest request
    ) {
        return updateCustomerUseCase.execute(
                customerWebMapper.toCommand(customerId, request)
        ).map(customerWebMapper::toResponse);
    }

    @ResponseStatus(HttpStatus.NO_CONTENT)
    @DeleteMapping("/{customerId}")
    public Mono<Void> deleteCustomer(@PathVariable UUID customerId) {
        return deleteCustomerUseCase.execute(customerId);
    }

    @ResponseStatus(HttpStatus.NO_CONTENT)
    @PatchMapping("/{customerId}/deactivate")
    public Mono<Void> deactivateCustomer(
            @PathVariable UUID customerId
    ) {
        return deactivateCustomerUseCase.execute(
                customerWebMapper.toCommand(customerId)
        );
    }

    @PatchMapping("/{customerId}/segment")
    public Mono<CustomerResponse> updateCustomerSegment(
            @PathVariable UUID customerId,
            @Valid @RequestBody CustomerSegmentUpdateRequest request
    ) {
        return assignCustomerSegmentUseCase.execute(customerId, request.segment())
                .map(customerWebMapper::toResponse);
    }

    @PostMapping("/{customerId}/evaluate-segment")
    public Mono<CustomerResponse> evaluateCustomerSegment(
            @PathVariable UUID customerId,
            @RequestParam(defaultValue = "0") int completedOrders,
            @RequestParam(defaultValue = "0.0") double totalSpent
    ) {
        return assignCustomerSegmentUseCase.evaluateAndAssign(customerId, completedOrders, totalSpent)
                .map(customerWebMapper::toResponse);
    }

    @GetMapping("/report")
    public Mono<CustomerReportResponse> getCustomerReport() {
        return getCustomerReportUseCase.execute()
                .map(customerWebMapper::toResponse);
    }

    @GetMapping("/statistic")
    public Mono<CustomerStatisticResponse> getCustomerStatistic() {
        return getCustomerStatisticUseCase.execute()
                .map(customerWebMapper::toResponse);
    }

    @GetMapping("/statistics")
    public Mono<CustomerStatisticResponse> getCustomerStatistics() {
        return getCustomerStatistic();
    }
}
