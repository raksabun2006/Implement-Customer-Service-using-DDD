package co.istad.order.port.out;

import co.istad.order.domain.entity.Order;
import co.istad.order.domain.valueobject.OrderId;
import co.istad.order.domain.valueobject.TrackingId;

import java.util.Optional;
import java.util.UUID;

public interface OrderRepository {

    Order save(Order order);

    Optional<Order> findById(OrderId orderId);

    Optional<Order> findById(UUID orderId);

    Optional<Order> findByTrackingId(TrackingId trackingId);

    Optional<Order> findByTrackingId(UUID trackingId);
}
