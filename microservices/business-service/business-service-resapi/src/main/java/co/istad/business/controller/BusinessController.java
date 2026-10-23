package co.istad.business.controller;

import co.istad.business.dto.RegisterBusinessRequest;
import co.istad.business.dto.RegisterBusinessResponse;
import co.istad.business.mapper.BusinessWebMapper;
import co.istad.business.usecase.GetBusinessUseCase;
import co.istad.business.usecase.RegisterBusinessUseCase;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.Map;
import java.util.UUID;

@Slf4j
@RestController
@RequestMapping("/api/businesses")
@RequiredArgsConstructor
public class BusinessController {

    private final RegisterBusinessUseCase registerBusinessUseCase;
    private final GetBusinessUseCase getBusinessUseCase;
    private final BusinessWebMapper businessWebMapper;

    @Value("${server.port}")
    private int serverPort;

    @GetMapping("/instance-info")
    public Mono<Map<String, Object>> getInstanceInfo() {
        log.info("Handled /instance-info on port: {}", serverPort);
        return Mono.just(Map.of(
                "service", "business-service",
                "port", serverPort,
                "timestamp", System.currentTimeMillis()
        ));
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Mono<RegisterBusinessResponse> createBusiness(@Valid @RequestBody RegisterBusinessRequest request) {
        return registerBusinessUseCase.execute(businessWebMapper.toCommand(request))
                .map(businessWebMapper::toResponse);
    }

    @GetMapping("/{businessId}")
    public Mono<RegisterBusinessResponse> getBusiness(@PathVariable UUID businessId) {
        return getBusinessUseCase.findById(businessId)
                .map(businessWebMapper::toResponse);
    }

    @GetMapping
    public Flux<RegisterBusinessResponse> getAllBusinesses() {
        return getBusinessUseCase.findAll()
                .map(businessWebMapper::toResponse);
    }
}
