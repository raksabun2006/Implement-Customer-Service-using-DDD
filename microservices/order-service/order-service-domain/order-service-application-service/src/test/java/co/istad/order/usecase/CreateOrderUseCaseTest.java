package co.istad.order.usecase;

import co.istad.order.domain.entity.Order;
import co.istad.order.domain.exception.OrderDomainException;
import co.istad.order.domain.service.OrderDomainService;
import co.istad.order.domain.service.OrderDomainServiceImpl;
import co.istad.order.domain.valueobject.OrderStatus;
import co.istad.order.dto.create.CreateOrderCommand;
import co.istad.order.dto.create.OrderAddressCommand;
import co.istad.order.dto.create.OrderItemCommand;
import co.istad.order.dto.create.CreateOrderResult;
import co.istad.order.dto.external.*;
import co.istad.order.port.out.BusinessClientPort;
import co.istad.order.port.out.CustomerClientPort;
import co.istad.order.port.out.OrderRepository;
import co.istad.order.port.out.PaymentClientPort;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CreateOrderUseCaseTest {

    @Mock
    private OrderRepository orderRepository;

    @Mock
    private CustomerClientPort customerClientPort;

    @Mock
    private BusinessClientPort businessClientPort;

    @Mock
    private PaymentClientPort paymentClientPort;

    private OrderDomainService orderDomainService;
    private CreateOrderUseCase createOrderUseCase;

    private final UUID customerId = UUID.randomUUID();
    private final UUID businessId = UUID.randomUUID();
    private final UUID productId = UUID.randomUUID();

    @BeforeEach
    void setUp() {
        orderDomainService = new OrderDomainServiceImpl();
        createOrderUseCase = new CreateOrderUseCase(
                orderRepository,
                orderDomainService,
                customerClientPort,
                businessClientPort,
                paymentClientPort
        );
    }

    private CreateOrderCommand createSampleCommand() {
        return new CreateOrderCommand(
                customerId,
                businessId,
                new BigDecimal("50.00"),
                new OrderAddressCommand("123 Test St", "12345", "Phnom Penh"),
                List.of(new OrderItemCommand(productId, 2, new BigDecimal("25.00"), new BigDecimal("50.00")))
        );
    }

    @Test
    void shouldCreateAndPayOrderWhenCustomerAndBusinessValidAndPaymentCompleted() {
        CreateOrderCommand command = createSampleCommand();

        CustomerExternalDto customerDto = new CustomerExternalDto(customerId, "johndoe", "john@example.com", "ACTIVE");
        BusinessExternalDto businessDto = new BusinessExternalDto(businessId, "Coffee Shop", true,
                List.of(new ProductExternalDto(productId, "Latte", new BigDecimal("25.00"), true)));
        PaymentExternalResponse paymentDto = new PaymentExternalResponse(
                UUID.randomUUID(), customerId, UUID.randomUUID(), "COMPLETED", List.of()
        );

        when(customerClientPort.getCustomer(customerId)).thenReturn(Optional.of(customerDto));
        when(businessClientPort.getBusiness(businessId)).thenReturn(Optional.of(businessDto));
        when(paymentClientPort.processPayment(any())).thenReturn(paymentDto);
        when(orderRepository.save(any(Order.class))).thenAnswer(invocation -> invocation.getArgument(0));

        CreateOrderResult result = createOrderUseCase.execute(command);

        assertNotNull(result);
        assertNotNull(result.orderId());
        assertNotNull(result.orderTrackingId());
        assertEquals(OrderStatus.PAID, result.orderStatus());
        verify(orderRepository, times(1)).save(any(Order.class));
    }

    @Test
    void shouldCancelOrderWhenPaymentFails() {
        CreateOrderCommand command = createSampleCommand();

        CustomerExternalDto customerDto = new CustomerExternalDto(customerId, "johndoe", "john@example.com", "ACTIVE");
        BusinessExternalDto businessDto = new BusinessExternalDto(businessId, "Coffee Shop", true,
                List.of(new ProductExternalDto(productId, "Latte", new BigDecimal("25.00"), true)));
        PaymentExternalResponse paymentDto = new PaymentExternalResponse(
                UUID.randomUUID(), customerId, UUID.randomUUID(), "FAILED", List.of("Insufficient credit balance")
        );

        when(customerClientPort.getCustomer(customerId)).thenReturn(Optional.of(customerDto));
        when(businessClientPort.getBusiness(businessId)).thenReturn(Optional.of(businessDto));
        when(paymentClientPort.processPayment(any())).thenReturn(paymentDto);
        when(orderRepository.save(any(Order.class))).thenAnswer(invocation -> invocation.getArgument(0));

        CreateOrderResult result = createOrderUseCase.execute(command);

        assertNotNull(result);
        assertEquals(OrderStatus.CANCELLED, result.orderStatus());
        verify(orderRepository, times(1)).save(any(Order.class));
    }

    @Test
    void shouldThrowExceptionWhenCustomerNotFound() {
        CreateOrderCommand command = createSampleCommand();

        when(customerClientPort.getCustomer(customerId)).thenReturn(Optional.empty());

        assertThrows(OrderDomainException.class, () -> createOrderUseCase.execute(command));
        verify(orderRepository, never()).save(any());
        verify(paymentClientPort, never()).processPayment(any());
    }

    @Test
    void shouldThrowExceptionWhenBusinessNotActive() {
        CreateOrderCommand command = createSampleCommand();

        CustomerExternalDto customerDto = new CustomerExternalDto(customerId, "johndoe", "john@example.com", "ACTIVE");
        BusinessExternalDto businessDto = new BusinessExternalDto(businessId, "Coffee Shop", false, List.of());

        when(customerClientPort.getCustomer(customerId)).thenReturn(Optional.of(customerDto));
        when(businessClientPort.getBusiness(businessId)).thenReturn(Optional.of(businessDto));

        assertThrows(OrderDomainException.class, () -> createOrderUseCase.execute(command));
        verify(orderRepository, never()).save(any());
        verify(paymentClientPort, never()).processPayment(any());
    }
}
