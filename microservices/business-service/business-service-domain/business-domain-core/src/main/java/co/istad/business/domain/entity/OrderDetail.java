package co.istad.business.domain.entity;

import co.istad.Money;
import co.istad.business.domain.valueobject.OrderId;
import co.istad.business.domain.valueobject.OrderStatus;

import java.util.List;

public class OrderDetail {
    private final OrderId orderId;
    private OrderStatus orderStatus;
    private Money totalAmount;
    private final List<Product> products;

    public OrderDetail(OrderId orderId, OrderStatus orderStatus, Money totalAmount, List<Product> products) {
        this.orderId = orderId;
        this.orderStatus = orderStatus;
        this.totalAmount = totalAmount;
        this.products = products;
    }

    public OrderId getOrderId() {
        return orderId;
    }

    public OrderStatus getOrderStatus() {
        return orderStatus;
    }

    public Money getTotalAmount() {
        return totalAmount;
    }

    public List<Product> getProducts() {
        return products;
    }
}
