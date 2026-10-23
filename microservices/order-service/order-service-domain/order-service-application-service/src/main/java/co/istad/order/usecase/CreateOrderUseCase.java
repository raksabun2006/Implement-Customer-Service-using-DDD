package co.istad.order.usecase;

import co.istad.CustomerId;
import co.istad.Money;
import co.istad.order.domain.entity.Order;
import co.istad.order.domain.entity.OrderItem;
import co.istad.order.domain.entity.Product;
import co.istad.order.domain.event.OrderCreatedEvent;
import co.istad.order.domain.exception.OrderDomainException;
import co.istad.order.domain.service.OrderDomainService;
import co.istad.order.domain.valueobject.BusinessId;
import co.istad.order.domain.valueobject.ProductId;
import co.istad.order.domain.valueobject.StreetAddress;
import co.istad.order.dto.create.CreateOrderCommand;
import co.istad.order.dto.create.CreateOrderResult;
import co.istad.order.dto.external.BusinessExternalDto;
import co.istad.order.dto.external.CustomerExternalDto;
import co.istad.order.dto.external.PaymentExternalRequest;
import co.istad.order.dto.external.PaymentExternalResponse;
import co.istad.order.port.out.BusinessClientPort;
import co.istad.order.port.out.CustomerClientPort;
import co.istad.order.port.out.OrderRepository;
import co.istad.order.port.out.PaymentClientPort;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.UUID;

@Slf4j
@Component
@RequiredArgsConstructor
public class CreateOrderUseCase {

    private final OrderRepository orderRepository;
    private final OrderDomainService orderDomainService;
    private final CustomerClientPort customerClientPort;
    private final BusinessClientPort businessClientPort;
    private final PaymentClientPort paymentClientPort;

    public CreateOrderResult execute(CreateOrderCommand command) {
        log.info("Starting e-commerce order workflow for customer: {} at business: {}",
                command.customerId(), command.businessId());

        // 1. Retrieve customer information from Customer Service via Eureka
        if (customerClientPort != null) {
            CustomerExternalDto customer = customerClientPort.getCustomer(command.customerId())
                    .orElseThrow(() -> new OrderDomainException("Customer not found or unreachable in Customer Service: " + command.customerId()));
            log.info("Customer verified successfully via Eureka discovery: {}", customer.customerId());
        }

        // 2. Retrieve business & product information and check availability through Business Service via Eureka
        if (businessClientPort != null) {
            BusinessExternalDto business = businessClientPort.getBusiness(command.businessId())
                    .orElseThrow(() -> new OrderDomainException("Business not found or unreachable in Business Service: " + command.businessId()));
            if (!business.active()) {
                throw new OrderDomainException("Business is not active: " + command.businessId());
            }
            log.info("Business verified successfully via Eureka discovery: {}", business.name());
        }

        // 3. Create or process order using existing domain model
        Order order = orderCommandToOrder(command);
        OrderCreatedEvent orderCreatedEvent = orderDomainService.validateAndInitiateOrder(order);
        log.info("Order validated & initiated with id: {}, trackingId: {}",
                order.getId().value(), order.getTrackingId().value());

        // 4. Communicate with Payment Service to process payment via Eureka
        if (paymentClientPort != null) {
            PaymentExternalResponse paymentResponse = paymentClientPort.processPayment(
                    new PaymentExternalRequest(command.customerId(), order.getId().value(), command.price())
            );

            // 5. Order status reflects the actual result of the workflow
            if (paymentResponse != null && "COMPLETED".equalsIgnoreCase(paymentResponse.paymentStatus())) {
                order.pay();
                log.info("Payment successful for order {}. Order status changed to PAID.", order.getId().value());
            } else {
                List<String> failures = (paymentResponse != null && paymentResponse.failureMessages() != null)
                        ? paymentResponse.failureMessages()
                        : List.of("Payment processing failed or rejected");
                order.cancel(failures);
                log.warn("Payment rejected for order {}. Order status changed to CANCELLED. Reason: {}",
                        order.getId().value(), failures);
            }
        }

        Order savedOrder = orderRepository.save(order);
        log.info("Order saved successfully with id: {} and status: {}",
                savedOrder.getId().value(), savedOrder.getOrderStatus());

        return new CreateOrderResult(
                savedOrder.getId().value(),
                savedOrder.getTrackingId().value(),
                savedOrder.getOrderStatus(),
                "Order processed with status: " + savedOrder.getOrderStatus()
        );
    }

    private Order orderCommandToOrder(CreateOrderCommand command) {
        List<OrderItem> orderItems = command.items().stream()
                .map(itemCommand -> OrderItem.builder()
                        .product(new Product(new ProductId(itemCommand.productId()), null, new Money(itemCommand.price())))
                        .price(new Money(itemCommand.price()))
                        .quantity(itemCommand.quantity())
                        .subTotal(new Money(itemCommand.subTotal()))
                        .build())
                .toList();

        StreetAddress deliveryAddress = new StreetAddress(
                UUID.randomUUID(),
                command.address().street(),
                command.address().postalCode(),
                command.address().city()
        );

        return Order.builder()
                .customerId(new CustomerId(command.customerId()))
                .businessId(new BusinessId(command.businessId()))
                .deliveryAddress(deliveryAddress)
                .price(new Money(command.price()))
                .items(orderItems)
                .build();
    }
}
