package co.istad.business.usecase;

import co.istad.business.domain.entity.Business;
import co.istad.business.domain.exception.BusinessNotFoundException;
import co.istad.business.dto.register.ProductResult;
import co.istad.business.dto.register.RegisterBusinessResult;
import co.istad.business.port.out.BusinessRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.UUID;

@Slf4j
@Component
@RequiredArgsConstructor
public class GetBusinessUseCase {

    private final BusinessRepository businessRepository;

    public Mono<RegisterBusinessResult> findById(UUID businessId) {
        log.info("Fetching business with id: {}", businessId);
        return businessRepository.findById(businessId)
                .switchIfEmpty(Mono.error(new BusinessNotFoundException("Business not found with id: " + businessId)))
                .map(this::toResult);
    }

    public Flux<RegisterBusinessResult> findAll() {
        log.info("Fetching all businesses");
        return businessRepository.findAll()
                .map(this::toResult);
    }

    private RegisterBusinessResult toResult(Business business) {
        List<ProductResult> productResults = business.getProducts() != null
                ? business.getProducts().stream()
                .map(p -> new ProductResult(
                        p.getId().value(),
                        p.getName(),
                        p.getPrice().amount(),
                        p.isAvailable()
                ))
                .toList()
                : List.of();

        return new RegisterBusinessResult(
                business.getId().value(),
                business.getName(),
                business.getContact().email(),
                business.getContact().phone(),
                business.getContact().address(),
                business.isActive(),
                productResults,
                "Business fetched successfully"
        );
    }
}
