package co.istad.order.controller;

import co.istad.order.dto.CreateOrderRequest;
import co.istad.order.dto.CreateOrderResponse;
import co.istad.order.dto.TrackOrderResponse;
import co.istad.order.mapper.OrderWebMapper;
import co.istad.order.usecase.CreateOrderUseCase;
import co.istad.order.usecase.TrackOrderUseCase;
import co.istad.order.dto.track.TrackOrderQuery;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

import java.util.UUID;

@Slf4j
@RestController
@RequestMapping("/api/orders")
@RequiredArgsConstructor
public class OrderController {

    private final CreateOrderUseCase createOrderUseCase;
    private final TrackOrderUseCase trackOrderUseCase;
    private final OrderWebMapper orderWebMapper;
    @GetMapping("/flux")
    public Mono<String> flux() {
        log.info("Received request: GET /flux");
        return Mono.just("Hello from WebFlux");
    }
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Mono<CreateOrderResponse> createOrder(@Valid @RequestBody CreateOrderRequest request) {
        log.info("Creating order for customer: {} at business: {}", request.customerId(), request.businessId());
        return Mono.fromCallable(() -> createOrderUseCase.execute(orderWebMapper.toCommand(request)))
                .subscribeOn(Schedulers.boundedElastic())
                .doOnSuccess(result -> log.info("Order created with tracking id: {}", result.orderTrackingId()))
                .map(orderWebMapper::toResponse);
    }

    @GetMapping("/{trackingId}")
    public Mono<TrackOrderResponse> trackOrder(@PathVariable("trackingId") UUID trackingId) {
        log.info("Tracking order with tracking id: {}", trackingId);
        return Mono.fromCallable(() -> trackOrderUseCase.execute(new TrackOrderQuery(trackingId)))
                .subscribeOn(Schedulers.boundedElastic())
                .map(orderWebMapper::toResponse);
    }
}
