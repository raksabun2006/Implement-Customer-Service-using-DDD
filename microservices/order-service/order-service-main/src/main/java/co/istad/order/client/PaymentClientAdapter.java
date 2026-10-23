package co.istad.order.client;

import co.istad.order.dto.external.PaymentExternalRequest;
import co.istad.order.dto.external.PaymentExternalResponse;
import co.istad.order.port.out.PaymentClientPort;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

import java.time.Duration;
import java.util.List;

@Component
public class PaymentClientAdapter implements PaymentClientPort {

    private static final Logger log = LoggerFactory.getLogger(PaymentClientAdapter.class);
    private final WebClient.Builder loadBalancedWebClientBuilder;

    public PaymentClientAdapter(WebClient.Builder loadBalancedWebClientBuilder) {
        this.loadBalancedWebClientBuilder = loadBalancedWebClientBuilder;
    }

    @Override
    public PaymentExternalResponse processPayment(PaymentExternalRequest request) {
        log.info("Calling Payment Service via Eureka: http://payment-service/api/payments for order: {}", request.orderId());
        try {
            return loadBalancedWebClientBuilder.build()
                    .post()
                    .uri("http://payment-service/api/payments")
                    .bodyValue(request)
                    .retrieve()
                    .bodyToMono(PaymentExternalResponse.class)
                    .timeout(Duration.ofSeconds(5))
                    .block();
        } catch (Exception e) {
            log.error("Failed to process payment with payment-service for order {}: {}", request.orderId(), e.getMessage());
            return new PaymentExternalResponse(
                    null,
                    request.customerId(),
                    request.orderId(),
                    "FAILED",
                    List.of("Payment Service unavailable or communication failed: " + e.getMessage())
            );
        }
    }
}
