package co.istad.payment.repository;

import co.istad.payment.document.CreditHistoryDocument;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface CreditHistoryMongoRepository extends MongoRepository<CreditHistoryDocument, UUID> {

    List<CreditHistoryDocument> findByCustomerId(UUID customerId);
}
