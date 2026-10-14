package co.istad.business.repository;

import co.istad.business.document.BusinessDocument;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface BusinessReactiveMongoRepository extends ReactiveMongoRepository<BusinessDocument, UUID> {
}
