package co.istad.payment.repository;

import co.istad.payment.document.CreditEntryDocument;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface CreditEntryMongoRepository extends MongoRepository<CreditEntryDocument, UUID> {

    Optional<CreditEntryDocument> findByCustomerId(UUID customerId);
}
