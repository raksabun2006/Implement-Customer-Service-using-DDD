package co.istad.business.repository;

import co.istad.business.document.BusinessDocument;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Mono;

import java.util.UUID;

@Repository
public interface BusinessReactiveMongoRepository extends ReactiveMongoRepository<BusinessDocument, UUID> {

    Mono<Boolean> existsByName(String name);

    Mono<BusinessDocument> findByName(String name);
}
