package co.istad.order.controller;

import co.istad.order.dto.CreateOrderRequest;
import co.istad.order.dto.CreateOrderResponse;
import co.istad.order.dto.TrackOrderResponse;
import co.istad.order.dto.create.CreateOrderResult;
import co.istad.order.dto.track.TrackOrderQuery;
import co.istad.order.dto.track.TrackOrderResult;
import co.istad.order.mapper.OrderWebMapper;
import co.istad.order.usecase.CreateOrderUseCase;
import co.istad.order.usecase.TrackOrderUseCase;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@Slf4j
@RestController
@RequestMapping("/api/orders")
@RequiredArgsConstructor
public class OrderController {

    private final CreateOrderUseCase createOrderUseCase;
    private final TrackOrderUseCase trackOrderUseCase;
    private final OrderWebMapper orderWebMapper;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public CreateOrderResponse createOrder(@Valid @RequestBody CreateOrderRequest request) {
        log.info("Creating order for customer: {} at business: {}", request.customerId(), request.businessId());
        CreateOrderResult result = createOrderUseCase.execute(orderWebMapper.toCommand(request));
        log.info("Order created with tracking id: {}", result.orderTrackingId());
        return orderWebMapper.toResponse(result);
    }

    @GetMapping("/{trackingId}")
    public TrackOrderResponse trackOrder(@PathVariable UUID trackingId) {
        log.info("Tracking order with tracking id: {}", trackingId);
        TrackOrderResult result = trackOrderUseCase.execute(new TrackOrderQuery(trackingId));
        return orderWebMapper.toResponse(result);
    }
}
