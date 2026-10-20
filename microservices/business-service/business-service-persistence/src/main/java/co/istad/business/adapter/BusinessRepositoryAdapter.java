package co.istad.business.adapter;

import co.istad.business.document.BusinessDocument;
import co.istad.business.domain.entity.Business;
import co.istad.business.domain.valueobject.BusinessId;
import co.istad.business.mapper.BusinessPersistenceMapper;
import co.istad.business.port.out.BusinessRepository;
import co.istad.business.repository.BusinessReactiveMongoRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.UUID;

/**
 * Persistence adapter using Spring Data Reactive MongoDB to support business registration processing.
 * Implements the outbound port {@link BusinessRepository} defined in the domain/application layer.
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class BusinessRepositoryAdapter implements BusinessRepository {

    private final BusinessReactiveMongoRepository reactiveMongoRepository;
    private final BusinessPersistenceMapper businessPersistenceMapper;

    @Override
    public Mono<Business> save(Business business) {
        log.info("Saving business reactively to MongoDB: {}", business.getName());
        BusinessDocument document = businessPersistenceMapper.toDocument(business);
        return reactiveMongoRepository.save(document)
                .map(businessPersistenceMapper::toDomain)
                .doOnSuccess(saved -> log.info("Successfully persisted business with id: {}", saved.getId().value()));
    }

    @Override
    public Mono<Business> findById(BusinessId businessId) {
        if (businessId == null || businessId.value() == null) {
            return Mono.empty();
        }
        return findById(businessId.value());
    }

    @Override
    public Mono<Business> findById(UUID businessId) {
        if (businessId == null) {
            return Mono.empty();
        }
        log.info("Finding business by id reactively from MongoDB: {}", businessId);
        return reactiveMongoRepository.findById(businessId)
                .map(businessPersistenceMapper::toDomain);
    }

    @Override
    public Flux<Business> findAll() {
        log.info("Finding all businesses reactively from MongoDB");
        return reactiveMongoRepository.findAll()
                .map(businessPersistenceMapper::toDomain);
    }

    @Override
    public Mono<Boolean> existsByName(String name) {
        if (name == null || name.trim().isEmpty()) {
            return Mono.just(false);
        }
        log.info("Checking if business exists reactively by name: {}", name);
        return reactiveMongoRepository.existsByName(name);
    }

    @Override
    public Mono<Business> findByName(String name) {
        if (name == null || name.trim().isEmpty()) {
            return Mono.empty();
        }
        log.info("Finding business reactively by name: {}", name);
        return reactiveMongoRepository.findByName(name)
                .map(businessPersistenceMapper::toDomain);
    }
}
