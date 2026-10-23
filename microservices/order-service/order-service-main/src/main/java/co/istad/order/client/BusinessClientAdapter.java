package co.istad.order.client;

import co.istad.order.dto.external.BusinessExternalDto;
import co.istad.order.port.out.BusinessClientPort;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

import java.time.Duration;
import java.util.Optional;
import java.util.UUID;

@Component
public class BusinessClientAdapter implements BusinessClientPort {

    private static final Logger log = LoggerFactory.getLogger(BusinessClientAdapter.class);
    private final WebClient.Builder loadBalancedWebClientBuilder;

    public BusinessClientAdapter(WebClient.Builder loadBalancedWebClientBuilder) {
        this.loadBalancedWebClientBuilder = loadBalancedWebClientBuilder;
    }

    @Override
    public Optional<BusinessExternalDto> getBusiness(UUID businessId) {
        log.info("Calling Business Service via Eureka: http://business-service/api/businesses/{}", businessId);
        try {
            return loadBalancedWebClientBuilder.build()
                    .get()
                    .uri("http://business-service/api/businesses/{businessId}", businessId)
                    .retrieve()
                    .bodyToMono(BusinessExternalDto.class)
                    .timeout(Duration.ofSeconds(3))
                    .blockOptional();
        } catch (Exception e) {
            log.error("Failed to retrieve business {} from business-service: {}", businessId, e.getMessage());
            return Optional.empty();
        }
    }

    @Override
    public Optional<String> getRespondingInstance() {
        log.info("Calling Business Service load balancer demo: http://business-service/api/businesses/instance-info");
        try {
            return loadBalancedWebClientBuilder.build()
                    .get()
                    .uri("http://business-service/api/businesses/instance-info")
                    .retrieve()
                    .bodyToMono(String.class)
                    .timeout(Duration.ofSeconds(3))
                    .blockOptional();
        } catch (Exception e) {
            log.error("Failed to query instance-info from business-service: {}", e.getMessage());
            return Optional.empty();
        }
    }
}
