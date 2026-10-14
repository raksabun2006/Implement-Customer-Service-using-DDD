package co.istad.repository;

import co.istad.document.CustomerDocument;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface CustomerMongoRepository extends MongoRepository<CustomerDocument, UUID> {

    Optional<CustomerDocument> findByUsername(String username);
}
