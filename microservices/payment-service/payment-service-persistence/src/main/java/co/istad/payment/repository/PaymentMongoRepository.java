package co.istad.payment.repository;

import co.istad.payment.document.PaymentDocument;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface PaymentMongoRepository extends MongoRepository<PaymentDocument, UUID> {

    Optional<PaymentDocument> findByOrderId(UUID orderId);
}
