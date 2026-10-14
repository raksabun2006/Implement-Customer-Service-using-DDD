package co.istad.order.usecase;

import co.istad.order.domain.entity.Order;
import co.istad.order.domain.exception.OrderNotFoundException;
import co.istad.order.domain.valueobject.TrackingId;
import co.istad.order.dto.track.TrackOrderQuery;
import co.istad.order.dto.track.TrackOrderResult;
import co.istad.order.port.out.OrderRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class TrackOrderUseCase {

    private final OrderRepository orderRepository;

    public TrackOrderResult execute(TrackOrderQuery query) {
        log.info("Tracking order with tracking id: {}", query.orderTrackingId());

        Order order = orderRepository.findByTrackingId(new TrackingId(query.orderTrackingId()))
                .orElseThrow(() -> new OrderNotFoundException("Could not find order with tracking id: " + query.orderTrackingId()));

        return new TrackOrderResult(
                order.getTrackingId().value(),
                order.getOrderStatus(),
                order.getFailureMessages()
        );
    }
}
