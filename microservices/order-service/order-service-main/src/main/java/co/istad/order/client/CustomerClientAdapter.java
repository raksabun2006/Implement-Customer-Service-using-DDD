package co.istad.order.client;

import co.istad.order.dto.external.CustomerExternalDto;
import co.istad.order.port.out.CustomerClientPort;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

import java.time.Duration;
import java.util.Optional;
import java.util.UUID;

@Component
public class CustomerClientAdapter implements CustomerClientPort {

    private static final Logger log = LoggerFactory.getLogger(CustomerClientAdapter.class);
    private final WebClient.Builder loadBalancedWebClientBuilder;

    public CustomerClientAdapter(WebClient.Builder loadBalancedWebClientBuilder) {
        this.loadBalancedWebClientBuilder = loadBalancedWebClientBuilder;
    }

    @Override
    public Optional<CustomerExternalDto> getCustomer(UUID customerId) {
        log.info("Calling Customer Service via Eureka: http://customer-service/api/customers/{}", customerId);
        try {
            return loadBalancedWebClientBuilder.build()
                    .get()
                    .uri("http://customer-service/api/customers/{customerId}", customerId)
                    .retrieve()
                    .bodyToMono(CustomerExternalDto.class)
                    .timeout(Duration.ofSeconds(3))
                    .blockOptional();
        } catch (Exception e) {
            log.error("Failed to retrieve customer {} from customer-service: {}", customerId, e.getMessage());
            return Optional.empty();
        }
    }
}
