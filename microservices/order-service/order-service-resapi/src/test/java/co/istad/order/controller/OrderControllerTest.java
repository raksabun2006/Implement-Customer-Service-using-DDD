package co.istad.order.controller;

import co.istad.order.domain.valueobject.OrderStatus;
import co.istad.order.dto.CreateOrderRequest;
import co.istad.order.dto.CreateOrderResponse;
import co.istad.order.dto.OrderAddressRequest;
import co.istad.order.dto.OrderItemRequest;
import co.istad.order.dto.TrackOrderResponse;
import co.istad.order.dto.create.CreateOrderCommand;
import co.istad.order.dto.create.CreateOrderResult;
import co.istad.order.dto.track.TrackOrderQuery;
import co.istad.order.dto.track.TrackOrderResult;
import co.istad.order.exception.OrderGlobalExceptionHandler;
import co.istad.order.mapper.OrderWebMapper;
import co.istad.order.usecase.CreateOrderUseCase;
import co.istad.order.usecase.TrackOrderUseCase;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.reactive.server.WebTestClient;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class OrderControllerTest {

    @Mock
    private CreateOrderUseCase createOrderUseCase;

    @Mock
    private TrackOrderUseCase trackOrderUseCase;

    @Mock
    private OrderWebMapper orderWebMapper;

    private WebTestClient webTestClient;

    @BeforeEach
    void setUp() {
        OrderController orderController = new OrderController(createOrderUseCase, trackOrderUseCase, orderWebMapper, null);
        webTestClient = WebTestClient.bindToController(orderController)
                .controllerAdvice(new OrderGlobalExceptionHandler())
                .build();
    }

    @Test
    void shouldCreateOrderSuccessfully() {
        UUID orderId = UUID.randomUUID();
        UUID trackingId = UUID.randomUUID();
        UUID customerId = UUID.randomUUID();
        UUID businessId = UUID.randomUUID();
        UUID productId = UUID.randomUUID();

        CreateOrderRequest request = new CreateOrderRequest(
                customerId,
                businessId,
                new BigDecimal("50.00"),
                new OrderAddressRequest("123 Test Street", "12345", "Test City"),
                List.of(new OrderItemRequest(productId, 2, new BigDecimal("25.00"), new BigDecimal("50.00")))
        );

        CreateOrderResult result = new CreateOrderResult(orderId, trackingId, OrderStatus.PENDING, "Order created successfully");
        CreateOrderResponse response = new CreateOrderResponse(orderId, trackingId, OrderStatus.PENDING, "Order created successfully");

        when(orderWebMapper.toCommand(any(CreateOrderRequest.class))).thenReturn(org.mockito.Mockito.mock(CreateOrderCommand.class));
        when(createOrderUseCase.execute(any())).thenReturn(result);
        when(orderWebMapper.toResponse(result)).thenReturn(response);

        webTestClient.post()
                .uri("/api/orders")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(request)
                .exchange()
                .expectStatus().isCreated()
                .expectBody()
                .jsonPath("$.orderId").isEqualTo(orderId.toString())
                .jsonPath("$.orderTrackingId").isEqualTo(trackingId.toString())
                .jsonPath("$.orderStatus").isEqualTo("PENDING")
                .jsonPath("$.message").isEqualTo("Order created successfully");
    }

    @Test
    void shouldTrackOrderSuccessfully() {
        UUID trackingId = UUID.randomUUID();
        TrackOrderResult result = new TrackOrderResult(trackingId, OrderStatus.PENDING, List.of());
        TrackOrderResponse response = new TrackOrderResponse(trackingId, OrderStatus.PENDING, List.of());

        when(trackOrderUseCase.execute(any(TrackOrderQuery.class))).thenReturn(result);
        when(orderWebMapper.toResponse(result)).thenReturn(response);

        webTestClient.get()
                .uri("/api/orders/{trackingId}", trackingId)
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.orderTrackingId").isEqualTo(trackingId.toString())
                .jsonPath("$.orderStatus").isEqualTo("PENDING");
    }

    @Test
    void shouldReturnBadRequestWhenCreateOrderPayloadIsInvalid() {
        // Missing required fields
        webTestClient.post()
                .uri("/api/orders")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue("{}")
                .exchange()
                .expectStatus().isBadRequest()
                .expectBody()
                .jsonPath("$.status").isEqualTo(400)
                .jsonPath("$.message").isEqualTo("Validation failed");
    }

    @Test
    void shouldReturnFluxEndpointSuccessfully() {
        webTestClient.get()
                .uri("/api/orders/flux")
                .exchange()
                .expectStatus().isOk()
                .expectBody(String.class).isEqualTo("Hello from WebFlux");
    }

    @Test
    void shouldHandleTypeMismatchProperly() {
        webTestClient.get()
                .uri("/api/orders/invalid-uuid")
                .exchange()
                .expectStatus().isBadRequest()
                .expectBody()
                .jsonPath("$.status").isEqualTo(400);
    }

    @Test
    void shouldReturnNotFoundProperlyForMissingRoute() {
        webTestClient.get()
                .uri("/completely-unknown-route")
                .exchange()
                .expectStatus().isNotFound();
    }
}
