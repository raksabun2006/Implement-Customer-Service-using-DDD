package co.istad.repository;

import co.istad.document.CustomerDocument;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Mono;

import java.util.UUID;

@Repository
public interface CustomerMongoRepository extends ReactiveMongoRepository<CustomerDocument, UUID> {

    Mono<CustomerDocument> findByUsername(String username);

    Mono<Boolean> existsByUsername(String username);
}
