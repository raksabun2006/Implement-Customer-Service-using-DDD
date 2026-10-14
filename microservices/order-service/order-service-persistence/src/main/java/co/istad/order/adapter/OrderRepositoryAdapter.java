package co.istad.order.adapter;

import co.istad.order.document.OrderDocument;
import co.istad.order.domain.entity.Order;
import co.istad.order.domain.valueobject.OrderId;
import co.istad.order.domain.valueobject.TrackingId;
import co.istad.order.mapper.OrderPersistenceMapper;
import co.istad.order.port.out.OrderRepository;
import co.istad.order.repository.OrderMongoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Optional;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class OrderRepositoryAdapter implements OrderRepository {

    private final OrderMongoRepository orderMongoRepository;
    private final OrderPersistenceMapper orderPersistenceMapper;

    @Override
    public Order save(Order order) {
        OrderDocument document = orderPersistenceMapper.toDocument(order);
        OrderDocument savedDocument = orderMongoRepository.save(document);
        return orderPersistenceMapper.toDomain(savedDocument);
    }

    @Override
    public Optional<Order> findById(OrderId orderId) {
        if (orderId == null || orderId.value() == null) {
            return Optional.empty();
        }
        return findById(orderId.value());
    }

    @Override
    public Optional<Order> findById(UUID orderId) {
        if (orderId == null) {
            return Optional.empty();
        }
        return orderMongoRepository.findById(orderId)
                .map(orderPersistenceMapper::toDomain);
    }

    @Override
    public Optional<Order> findByTrackingId(TrackingId trackingId) {
        if (trackingId == null || trackingId.value() == null) {
            return Optional.empty();
        }
        return findByTrackingId(trackingId.value());
    }

    @Override
    public Optional<Order> findByTrackingId(UUID trackingId) {
        if (trackingId == null) {
            return Optional.empty();
        }
        return orderMongoRepository.findByTrackingId(trackingId)
                .map(orderPersistenceMapper::toDomain);
    }
}
