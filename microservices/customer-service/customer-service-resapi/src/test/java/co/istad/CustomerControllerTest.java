package co.istad;

import co.istad.controller.CustomerController;
import co.istad.dto.*;
import co.istad.mapper.CustomerWebMapper;
import co.istad.usecase.*;
import co.istad.valueObject.CustomerSegment;
import co.istad.valueObject.CustomerStatus;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mapstruct.factory.Mappers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CustomerControllerTest {

    @Mock
    private CreateCustomerUseCase createCustomerUseCase;
    @Mock
    private UpdateCustomerUseCase updateCustomerUseCase;
    @Mock
    private DeactivateCustomerUseCase deactivateCustomerUseCase;
    @Mock
    private DeleteCustomerUseCase deleteCustomerUseCase;
    @Mock
    private GetCustomerUseCase getCustomerUseCase;
    @Mock
    private AssignCustomerSegmentUseCase assignCustomerSegmentUseCase;
    @Mock
    private GetCustomerReportUseCase getCustomerReportUseCase;
    @Mock
    private GetCustomerStatisticUseCase getCustomerStatisticUseCase;

    @Spy
    private CustomerWebMapper customerWebMapper = Mappers.getMapper(CustomerWebMapper.class);

    @InjectMocks
    private CustomerController customerController;

    @Test
    @DisplayName("POST /api/customers - should initiate customer")
    void shouldInitiateCustomer() {
        UUID id = UUID.randomUUID();
        CustomerInitiateRequest request = new CustomerInitiateRequest(
                "john_doe", "Doe", "John", "john@example.com", "+85512345678", CustomerSegment.SILVER
        );
        InitiateCustomerResult result = new InitiateCustomerResult(
                id, "john_doe", "Doe", "John", "john@example.com", "+85512345678",
                CustomerStatus.ACTIVE, CustomerSegment.SILVER, Instant.now()
        );

        when(createCustomerUseCase.execute(any())).thenReturn(Mono.just(result));

        StepVerifier.create(customerController.initiateCustomer(request))
                .assertNext(response -> {
                    assertThat(response.customerId()).isEqualTo(id);
                    assertThat(response.username()).isEqualTo("john_doe");
                    assertThat(response.segment()).isEqualTo(CustomerSegment.SILVER);
                })
                .verifyComplete();
    }

    @Test
    @DisplayName("GET /api/customers - should get filtered paginated customers")
    void shouldGetFilteredCustomers() {
        CustomerResult cr = new CustomerResult(
                UUID.randomUUID(), "john_doe", "Doe", "John", "john@example.com", "+85512345678",
                CustomerStatus.ACTIVE, CustomerSegment.SILVER, Instant.now()
        );
        PageResult<CustomerResult> pageResult = PageResult.of(List.of(cr), 0, 10, 1L);

        when(getCustomerUseCase.findByFilter(any())).thenReturn(Mono.just(pageResult));

        StepVerifier.create(customerController.getCustomers(
                "john", CustomerStatus.ACTIVE, CustomerSegment.SILVER,
                null, null, null, null, 0, 10, "createdAt", "desc"
        ))
                .assertNext(response -> {
                    assertThat(response.totalElements()).isEqualTo(1L);
                    assertThat(response.content()).hasSize(1);
                    assertThat(response.content().get(0).username()).isEqualTo("john_doe");
                })
                .verifyComplete();
    }

    @Test
    @DisplayName("GET /api/customers/{customerId} - should get customer by ID")
    void shouldGetCustomerById() {
        UUID id = UUID.randomUUID();
        CustomerResult cr = new CustomerResult(
                id, "john_doe", "Doe", "John", "john@example.com", "+85512345678",
                CustomerStatus.ACTIVE, CustomerSegment.SILVER, Instant.now()
        );

        when(getCustomerUseCase.findById(id)).thenReturn(Mono.just(cr));

        StepVerifier.create(customerController.getCustomerById(id))
                .assertNext(response -> {
                    assertThat(response.customerId()).isEqualTo(id);
                    assertThat(response.username()).isEqualTo("john_doe");
                })
                .verifyComplete();
    }

    @Test
    @DisplayName("PUT /api/customers/{customerId} - should update customer")
    void shouldUpdateCustomer() {
        UUID id = UUID.randomUUID();
        CustomerUpdateRequest request = new CustomerUpdateRequest("Smith", "Johnny", CustomerSegment.GOLD);
        UpdateCustomerResult result = new UpdateCustomerResult(
                id, "john_doe", "Smith", "Johnny", "john@example.com", "+85512345678",
                CustomerStatus.ACTIVE, CustomerSegment.GOLD, Instant.now()
        );

        when(updateCustomerUseCase.execute(any())).thenReturn(Mono.just(result));

        StepVerifier.create(customerController.updateCustomer(id, request))
                .assertNext(response -> {
                    assertThat(response.familyName()).isEqualTo("Smith");
                    assertThat(response.givenName()).isEqualTo("Johnny");
                    assertThat(response.segment()).isEqualTo(CustomerSegment.GOLD);
                })
                .verifyComplete();
    }

    @Test
    @DisplayName("DELETE /api/customers/{customerId} - should delete customer")
    void shouldDeleteCustomer() {
        UUID id = UUID.randomUUID();
        when(deleteCustomerUseCase.execute(id)).thenReturn(Mono.empty());

        StepVerifier.create(customerController.deleteCustomer(id))
                .verifyComplete();

        verify(deleteCustomerUseCase).execute(id);
    }

    @Test
    @DisplayName("PATCH /api/customers/{customerId}/segment - should update segment")
    void shouldUpdateSegment() {
        UUID id = UUID.randomUUID();
        CustomerSegmentUpdateRequest request = new CustomerSegmentUpdateRequest(CustomerSegment.PREMIUM);
        CustomerResult result = new CustomerResult(
                id, "john_doe", "Doe", "John", "john@example.com", "+85512345678",
                CustomerStatus.ACTIVE, CustomerSegment.PREMIUM, Instant.now()
        );

        when(assignCustomerSegmentUseCase.execute(id, CustomerSegment.PREMIUM)).thenReturn(Mono.just(result));

        StepVerifier.create(customerController.updateCustomerSegment(id, request))
                .assertNext(response -> assertThat(response.segment()).isEqualTo(CustomerSegment.PREMIUM))
                .verifyComplete();
    }

    @Test
    @DisplayName("POST /api/customers/{customerId}/evaluate-segment - should evaluate segment")
    void shouldEvaluateSegment() {
        UUID id = UUID.randomUUID();
        CustomerResult result = new CustomerResult(
                id, "spender", "Big", "Spender", "spender@example.com", "+85512345678",
                CustomerStatus.ACTIVE, CustomerSegment.PREMIUM, Instant.now()
        );

        when(assignCustomerSegmentUseCase.evaluateAndAssign(id, 55, 2500.0)).thenReturn(Mono.just(result));

        StepVerifier.create(customerController.evaluateCustomerSegment(id, 55, 2500.0))
                .assertNext(response -> assertThat(response.segment()).isEqualTo(CustomerSegment.PREMIUM))
                .verifyComplete();
    }

    @Test
    @DisplayName("GET /api/customers/report - should return customer report")
    void shouldGetReport() {
        CustomerReportResult reportResult = new CustomerReportResult(
                10L,
                Map.of(CustomerStatus.ACTIVE, 8L, CustomerStatus.INACTIVE, 2L),
                Map.of(CustomerSegment.SILVER, 5L, CustomerSegment.GOLD, 3L, CustomerSegment.PREMIUM, 2L),
                Map.of("ACTIVE_SILVER", 5L)
        );

        when(getCustomerReportUseCase.execute()).thenReturn(Mono.just(reportResult));

        StepVerifier.create(customerController.getCustomerReport())
                .assertNext(response -> {
                    assertThat(response.totalCustomers()).isEqualTo(10L);
                    assertThat(response.countByStatus()).containsEntry(CustomerStatus.ACTIVE, 8L);
                })
                .verifyComplete();
    }

    @Test
    @DisplayName("GET /api/customers/statistic - should return customer statistics")
    void shouldGetStatistic() {
        CustomerStatisticResult statisticResult = new CustomerStatisticResult(
                20L, 18L, 2L, 10L, 7L, 3L,
                Map.of(CustomerStatus.ACTIVE, 18L, CustomerStatus.INACTIVE, 2L),
                Map.of(CustomerSegment.SILVER, 10L, CustomerSegment.GOLD, 7L, CustomerSegment.PREMIUM, 3L),
                Instant.now()
        );

        when(getCustomerStatisticUseCase.execute()).thenReturn(Mono.just(statisticResult));

        StepVerifier.create(customerController.getCustomerStatistic())
                .assertNext(response -> {
                    assertThat(response.totalCustomers()).isEqualTo(20L);
                    assertThat(response.activeCustomers()).isEqualTo(18L);
                    assertThat(response.silverCustomers()).isEqualTo(10L);
                })
                .verifyComplete();
    }
}
