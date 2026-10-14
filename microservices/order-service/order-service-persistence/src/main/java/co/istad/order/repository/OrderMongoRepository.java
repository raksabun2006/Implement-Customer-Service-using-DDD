package co.istad.order.repository;

import co.istad.order.document.OrderDocument;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface OrderMongoRepository extends MongoRepository<OrderDocument, UUID> {

    Optional<OrderDocument> findByTrackingId(UUID trackingId);
}
