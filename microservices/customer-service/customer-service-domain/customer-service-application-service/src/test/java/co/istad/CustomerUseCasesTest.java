package co.istad;

import co.istad.dto.*;
import co.istad.entity.Customer;
import co.istad.event.CustomerInitiatedEvent;
import co.istad.event.CustomerSegmentAssignedEvent;
import co.istad.event.CustomerUpdatedEvent;
import co.istad.exception.CustomerAlreadyExistsException;
import co.istad.exception.CustomerNotFoundException;
import co.istad.port.out.CustomerRepository;
import co.istad.service.CustomerDomainService;
import co.istad.service.CustomerSegmentationService;
import co.istad.service.CustomerSegmentationServiceImpl;
import co.istad.usecase.*;
import co.istad.valueObject.CustomerSegment;
import co.istad.valueObject.CustomerStatus;
import co.istad.valueObject.Email;
import co.istad.valueObject.PhoneNumber;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.time.Instant;
import java.time.ZonedDateTime;
import java.util.Map;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CustomerUseCasesTest {

    @Mock
    private CustomerRepository customerRepository;

    @Mock
    private CustomerDomainService customerDomainService;

    private CustomerSegmentationService customerSegmentationService;

    private CreateCustomerUseCase createCustomerUseCase;
    private GetCustomerUseCase getCustomerUseCase;
    private UpdateCustomerUseCaseImpl updateCustomerUseCase;
    private DeleteCustomerUseCaseImpl deleteCustomerUseCase;
    private AssignCustomerSegmentUseCaseImpl assignCustomerSegmentUseCase;
    private GetCustomerReportUseCaseImpl getCustomerReportUseCase;
    private GetCustomerStatisticUseCaseImpl getCustomerStatisticUseCase;

    @BeforeEach
    void setUp() {
        customerSegmentationService = new CustomerSegmentationServiceImpl();
        createCustomerUseCase = new CreateCustomerUseCase(customerRepository, customerDomainService, customerSegmentationService);
        getCustomerUseCase = new GetCustomerUseCase(customerRepository);
        updateCustomerUseCase = new UpdateCustomerUseCaseImpl(customerRepository, customerDomainService);
        deleteCustomerUseCase = new DeleteCustomerUseCaseImpl(customerRepository);
        assignCustomerSegmentUseCase = new AssignCustomerSegmentUseCaseImpl(customerRepository, customerDomainService, customerSegmentationService);
        getCustomerReportUseCase = new GetCustomerReportUseCaseImpl(customerRepository);
        getCustomerStatisticUseCase = new GetCustomerStatisticUseCaseImpl(customerRepository);
    }

    @Test
    @DisplayName("CreateCustomerUseCase - should successfully create a customer")
    void shouldCreateCustomerSuccessfully() {
        InitiateCustomerCommand command = new InitiateCustomerCommand(
                "sarah_connor", "Connor", "Sarah", "sarah@skynet.com", "+1234567890", CustomerSegment.SILVER
        );

        when(customerRepository.existsByUsername("sarah_connor")).thenReturn(Mono.just(false));
        when(customerDomainService.initiateCustomer(any(Customer.class))).thenAnswer(invocation -> {
            Customer c = invocation.getArgument(0);
            c.initiateCustomer();
            return new CustomerInitiatedEvent(c, ZonedDateTime.now());
        });
        when(customerRepository.save(any(Customer.class))).thenAnswer(inv -> Mono.just(inv.getArgument(0)));

        StepVerifier.create(createCustomerUseCase.execute(command))
                .assertNext(result -> {
                    assertThat(result.customerId()).isNotNull();
                    assertThat(result.username()).isEqualTo("sarah_connor");
                    assertThat(result.status()).isEqualTo(CustomerStatus.ACTIVE);
                    assertThat(result.segment()).isEqualTo(CustomerSegment.SILVER);
                })
                .verifyComplete();
    }

    @Test
    @DisplayName("CreateCustomerUseCase - should throw error when username already exists")
    void shouldFailWhenUsernameAlreadyExists() {
        InitiateCustomerCommand command = new InitiateCustomerCommand(
                "existing_user", "Doe", "John", "john@example.com", "+1234567890", null
        );

        when(customerRepository.existsByUsername("existing_user")).thenReturn(Mono.just(true));

        StepVerifier.create(createCustomerUseCase.execute(command))
                .expectError(CustomerAlreadyExistsException.class)
                .verify();
    }

    @Test
    @DisplayName("GetCustomerUseCase - should return customer by id")
    void shouldFindCustomerById() {
        UUID customerId = UUID.randomUUID();
        Customer customer = Customer.builder()
                .id(new CustomerId(customerId))
                .username("jane_doe")
                .familyName("Doe")
                .givenName("Jane")
                .email(new Email("jane@example.com"))
                .phoneNumber(new PhoneNumber("+85511223344"))
                .status(CustomerStatus.ACTIVE)
                .segment(CustomerSegment.GOLD)
                .createdAt(Instant.now())
                .build();

        when(customerRepository.findById(new CustomerId(customerId))).thenReturn(Mono.just(customer));

        StepVerifier.create(getCustomerUseCase.findById(customerId))
                .assertNext(res -> {
                    assertThat(res.customerId()).isEqualTo(customerId);
                    assertThat(res.username()).isEqualTo("jane_doe");
                    assertThat(res.segment()).isEqualTo(CustomerSegment.GOLD);
                })
                .verifyComplete();
    }

    @Test
    @DisplayName("GetCustomerUseCase - should throw CustomerNotFoundException when customer not found")
    void shouldThrowNotFoundWhenCustomerDoesNotExist() {
        UUID customerId = UUID.randomUUID();
        when(customerRepository.findById(new CustomerId(customerId))).thenReturn(Mono.empty());

        StepVerifier.create(getCustomerUseCase.findById(customerId))
                .expectError(CustomerNotFoundException.class)
                .verify();
    }

    @Test
    @DisplayName("GetCustomerUseCase - should filter, paginate, and sort customers")
    void shouldFilterCustomersWithPagination() {
        CustomerQueryFilter filter = CustomerQueryFilter.of(
                "doe", CustomerStatus.ACTIVE, CustomerSegment.SILVER,
                null, null, 0, 10, "createdAt", "desc"
        );

        Customer c1 = Customer.builder()
                .id(new CustomerId(UUID.randomUUID()))
                .username("john_doe")
                .familyName("Doe")
                .givenName("John")
                .email(new Email("john@example.com"))
                .phoneNumber(new PhoneNumber("+85512345678"))
                .status(CustomerStatus.ACTIVE)
                .segment(CustomerSegment.SILVER)
                .createdAt(Instant.now())
                .build();

        when(customerRepository.findByFilter(filter)).thenReturn(Flux.just(c1));
        when(customerRepository.countByFilter(filter)).thenReturn(Mono.just(1L));

        StepVerifier.create(getCustomerUseCase.findByFilter(filter))
                .assertNext(page -> {
                    assertThat(page.totalElements()).isEqualTo(1L);
                    assertThat(page.page()).isEqualTo(0);
                    assertThat(page.content()).hasSize(1);
                    assertThat(page.content().get(0).username()).isEqualTo("john_doe");
                })
                .verifyComplete();
    }

    @Test
    @DisplayName("UpdateCustomerUseCase - should update customer details and segment")
    void shouldUpdateCustomer() {
        UUID customerId = UUID.randomUUID();
        Customer customer = Customer.builder()
                .id(new CustomerId(customerId))
                .username("john_doe")
                .familyName("OldFamily")
                .givenName("OldGiven")
                .email(new Email("john@example.com"))
                .phoneNumber(new PhoneNumber("+85512345678"))
                .status(CustomerStatus.ACTIVE)
                .segment(CustomerSegment.SILVER)
                .createdAt(Instant.now())
                .build();

        when(customerRepository.findById(new CustomerId(customerId))).thenReturn(Mono.just(customer));
        when(customerDomainService.updateCustomer(any())).thenReturn(new CustomerUpdatedEvent(customer, ZonedDateTime.now()));
        when(customerRepository.save(any())).thenAnswer(inv -> Mono.just(inv.getArgument(0)));

        UpdateCustomerCommand command = new UpdateCustomerCommand(customerId, "NewFamily", "NewGiven", CustomerSegment.PREMIUM);

        StepVerifier.create(updateCustomerUseCase.execute(command))
                .assertNext(res -> {
                    assertThat(res.familyName()).isEqualTo("NewFamily");
                    assertThat(res.givenName()).isEqualTo("NewGiven");
                    assertThat(res.segment()).isEqualTo(CustomerSegment.PREMIUM);
                })
                .verifyComplete();
    }

    @Test
    @DisplayName("DeleteCustomerUseCase - should successfully delete customer")
    void shouldDeleteCustomer() {
        UUID customerId = UUID.randomUUID();
        Customer customer = Customer.builder()
                .id(new CustomerId(customerId))
                .username("to_delete")
                .familyName("Delete")
                .givenName("User")
                .email(new Email("del@example.com"))
                .phoneNumber(new PhoneNumber("+85512345678"))
                .status(CustomerStatus.ACTIVE)
                .segment(CustomerSegment.SILVER)
                .build();

        when(customerRepository.findById(new CustomerId(customerId))).thenReturn(Mono.just(customer));
        when(customerRepository.deleteById(new CustomerId(customerId))).thenReturn(Mono.empty());

        StepVerifier.create(deleteCustomerUseCase.execute(customerId))
                .verifyComplete();

        verify(customerRepository, times(1)).deleteById(new CustomerId(customerId));
    }

    @Test
    @DisplayName("AssignCustomerSegmentUseCase - should evaluate and assign segment based on orders and spending")
    void shouldEvaluateAndAssignSegment() {
        UUID customerId = UUID.randomUUID();
        Customer customer = Customer.builder()
                .id(new CustomerId(customerId))
                .username("spender")
                .familyName("Big")
                .givenName("Spender")
                .email(new Email("spender@example.com"))
                .phoneNumber(new PhoneNumber("+85512345678"))
                .status(CustomerStatus.ACTIVE)
                .segment(CustomerSegment.SILVER)
                .createdAt(Instant.now())
                .build();

        when(customerRepository.findById(new CustomerId(customerId))).thenReturn(Mono.just(customer));
        when(customerDomainService.assignSegment(any(), eq(CustomerSegment.PREMIUM))).thenAnswer(inv -> {
            Customer c = inv.getArgument(0);
            CustomerSegment seg = inv.getArgument(1);
            c.assignSegment(seg);
            return new CustomerSegmentAssignedEvent(c, seg, ZonedDateTime.now());
        });
        when(customerRepository.save(any())).thenAnswer(inv -> Mono.just(inv.getArgument(0)));

        // 60 orders and 3000 spent -> should evaluate to PREMIUM
        StepVerifier.create(assignCustomerSegmentUseCase.evaluateAndAssign(customerId, 60, 3000.0))
                .assertNext(res -> assertThat(res.segment()).isEqualTo(CustomerSegment.PREMIUM))
                .verifyComplete();
    }

    @Test
    @DisplayName("GetCustomerReportUseCase - should retrieve aggregation report")
    void shouldGetCustomerReport() {
        CustomerReportResult reportResult = new CustomerReportResult(
                10L,
                Map.of(CustomerStatus.ACTIVE, 8L, CustomerStatus.INACTIVE, 2L),
                Map.of(CustomerSegment.SILVER, 5L, CustomerSegment.GOLD, 3L, CustomerSegment.PREMIUM, 2L),
                Map.of("ACTIVE_SILVER", 5L, "ACTIVE_GOLD", 3L, "INACTIVE_PREMIUM", 2L)
        );

        when(customerRepository.getCustomerReport()).thenReturn(Mono.just(reportResult));

        StepVerifier.create(getCustomerReportUseCase.execute())
                .assertNext(r -> {
                    assertThat(r.totalCustomers()).isEqualTo(10L);
                    assertThat(r.countByStatus()).containsEntry(CustomerStatus.ACTIVE, 8L);
                    assertThat(r.countBySegment()).containsEntry(CustomerSegment.PREMIUM, 2L);
                })
                .verifyComplete();
    }

    @Test
    @DisplayName("GetCustomerStatisticUseCase - should retrieve statistics")
    void shouldGetCustomerStatistic() {
        CustomerStatisticResult statisticResult = new CustomerStatisticResult(
                20L, 18L, 2L, 10L, 7L, 3L,
                Map.of(CustomerStatus.ACTIVE, 18L, CustomerStatus.INACTIVE, 2L),
                Map.of(CustomerSegment.SILVER, 10L, CustomerSegment.GOLD, 7L, CustomerSegment.PREMIUM, 3L),
                Instant.now()
        );

        when(customerRepository.getCustomerStatistic()).thenReturn(Mono.just(statisticResult));

        StepVerifier.create(getCustomerStatisticUseCase.execute())
                .assertNext(s -> {
                    assertThat(s.totalCustomers()).isEqualTo(20L);
                    assertThat(s.activeCustomers()).isEqualTo(18L);
                    assertThat(s.goldCustomers()).isEqualTo(7L);
                })
                .verifyComplete();
    }
}
