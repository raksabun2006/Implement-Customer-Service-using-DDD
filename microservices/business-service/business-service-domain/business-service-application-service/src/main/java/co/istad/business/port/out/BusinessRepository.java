package co.istad.business.port.out;

import co.istad.business.domain.entity.Business;
import co.istad.business.domain.valueobject.BusinessId;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.UUID;

public interface BusinessRepository {

    Mono<Business> save(Business business);

    Mono<Business> findById(BusinessId businessId);

    Mono<Business> findById(UUID businessId);

    Flux<Business> findAll();

    Mono<Boolean> existsByName(String name);

    Mono<Business> findByName(String name);
}
