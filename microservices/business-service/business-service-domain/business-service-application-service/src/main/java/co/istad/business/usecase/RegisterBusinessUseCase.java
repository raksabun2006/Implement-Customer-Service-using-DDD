package co.istad.business.usecase;

import co.istad.Money;
import co.istad.business.domain.entity.Business;
import co.istad.business.domain.entity.Product;
import co.istad.business.domain.event.BusinessRegisteredEvent;
import co.istad.business.domain.exception.BusinessAlreadyExistsException;
import co.istad.business.domain.service.BusinessDomainService;
import co.istad.business.domain.valueobject.BusinessContact;
import co.istad.business.domain.valueobject.ProductId;
import co.istad.business.dto.register.ProductResult;
import co.istad.business.dto.register.RegisterBusinessCommand;
import co.istad.business.dto.register.RegisterBusinessResult;
import co.istad.business.port.out.BusinessRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Slf4j
@Component
@RequiredArgsConstructor
public class RegisterBusinessUseCase {

    private final BusinessRepository businessRepository;
    private final BusinessDomainService businessDomainService;

    public Mono<RegisterBusinessResult> execute(RegisterBusinessCommand command) {
        log.info("Executing business registration for: {}", command.name());

        return businessRepository.existsByName(command.name())
                .flatMap(exists -> {
                    if (Boolean.TRUE.equals(exists)) {
                        log.warn("Business with name '{}' already exists", command.name());
                        return Mono.error(new BusinessAlreadyExistsException(
                                "Business with name '" + command.name() + "' already exists!"
                        ));
                    }
                    return Mono.fromCallable(() -> {
                        Business business = toBusinessDomain(command);
                        BusinessRegisteredEvent event = businessDomainService.validateAndInitiateBusiness(business);
                        log.info("Business registered event initiated at: {}", event.getCreatedAt());
                        return business;
                    }).flatMap(businessRepository::save);
                })
                .map(this::toRegisterBusinessResult)
                .doOnSuccess(result -> log.info("Business registered successfully with id: {}", result.businessId()))
                .doOnError(error -> log.error("Failed to register business: {}", error.getMessage(), error));
    }

    private Business toBusinessDomain(RegisterBusinessCommand command) {
        List<Product> products = new ArrayList<>();
        if (command.products() != null) {
            products = command.products().stream()
                    .map(p -> new Product(
                            new ProductId(p.productId() != null ? p.productId() : UUID.randomUUID()),
                            p.name(),
                            new Money(p.price()),
                            p.available()
                    ))
                    .toList();
        }

        BusinessContact contact = new BusinessContact(
                command.email(),
                command.phone(),
                command.address()
        );

        return Business.builder()
                .name(command.name())
                .contact(contact)
                .products(products)
                .build();
    }

    private RegisterBusinessResult toRegisterBusinessResult(Business business) {
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
                "Business registered successfully"
        );
    }
}
