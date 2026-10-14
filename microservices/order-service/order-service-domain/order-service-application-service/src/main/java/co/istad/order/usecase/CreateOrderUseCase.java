package co.istad.order.usecase;

import co.istad.CustomerId;
import co.istad.Money;
import co.istad.order.domain.entity.Order;
import co.istad.order.domain.entity.OrderItem;
import co.istad.order.domain.entity.Product;
import co.istad.order.domain.event.OrderCreatedEvent;
import co.istad.order.domain.service.OrderDomainService;
import co.istad.order.domain.valueobject.BusinessId;
import co.istad.order.domain.valueobject.ProductId;
import co.istad.order.domain.valueobject.StreetAddress;
import co.istad.order.dto.create.CreateOrderCommand;
import co.istad.order.dto.create.CreateOrderResult;
import co.istad.order.port.out.OrderRepository;
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

    public CreateOrderResult execute(CreateOrderCommand command) {
        log.info("Creating order for customer: {} at business: {}", command.customerId(), command.businessId());

        Order order = orderCommandToOrder(command);
        OrderCreatedEvent orderCreatedEvent = orderDomainService.validateAndInitiateOrder(order);
        log.info("Order created event produced at: {}", orderCreatedEvent.getCreatedAt());

        Order savedOrder = orderRepository.save(order);
        log.info("Order is saved with id: {}", savedOrder.getId().value());

        return new CreateOrderResult(
                savedOrder.getId().value(),
                savedOrder.getTrackingId().value(),
                savedOrder.getOrderStatus(),
                "Order created successfully"
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
