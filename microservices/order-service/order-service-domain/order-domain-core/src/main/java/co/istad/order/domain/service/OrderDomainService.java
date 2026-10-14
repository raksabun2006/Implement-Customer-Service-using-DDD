package co.istad.order.domain.service;

import co.istad.order.domain.entity.Order;
import co.istad.order.domain.event.OrderCancelledEvent;
import co.istad.order.domain.event.OrderCreatedEvent;
import co.istad.order.domain.event.OrderPaidEvent;

import java.util.List;

public interface OrderDomainService {

    OrderCreatedEvent validateAndInitiateOrder(Order order);

    OrderPaidEvent payOrder(Order order);

    void approveOrder(Order order);

    OrderCancelledEvent cancelOrderPayment(Order order, List<String> failureMessages);

    void cancelOrder(Order order, List<String> failureMessages);
}
