package co.istad.order.mapper;

import co.istad.CustomerId;
import co.istad.Money;
import co.istad.order.document.OrderAddressDocument;
import co.istad.order.document.OrderDocument;
import co.istad.order.document.OrderItemDocument;
import co.istad.order.domain.entity.Order;
import co.istad.order.domain.entity.OrderItem;
import co.istad.order.domain.entity.Product;
import co.istad.order.domain.valueobject.*;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class OrderPersistenceMapper {

    public OrderDocument toDocument(Order order) {
        if (order == null) {
            return null;
        }

        OrderAddressDocument addressDoc = null;
        if (order.getDeliveryAddress() != null) {
            addressDoc = OrderAddressDocument.builder()
                    .id(order.getDeliveryAddress().id())
                    .street(order.getDeliveryAddress().street())
                    .postalCode(order.getDeliveryAddress().postalCode())
                    .city(order.getDeliveryAddress().city())
                    .build();
        }

        List<OrderItemDocument> itemDocs = new ArrayList<>();
        if (order.getItems() != null) {
            for (OrderItem item : order.getItems()) {
                OrderItemDocument itemDoc = OrderItemDocument.builder()
                        .id(item.getId() != null ? item.getId().value() : null)
                        .productId(item.getProduct().getId().value())
                        .price(item.getPrice().amount())
                        .quantity(item.getQuantity())
                        .subTotal(item.getSubTotal().amount())
                        .build();
                itemDocs.add(itemDoc);
            }
        }

        return OrderDocument.builder()
                .id(order.getId() != null ? order.getId().value() : null)
                .customerId(order.getCustomerId() != null ? order.getCustomerId().value() : null)
                .businessId(order.getBusinessId() != null ? order.getBusinessId().value() : null)
                .trackingId(order.getTrackingId() != null ? order.getTrackingId().value() : null)
                .price(order.getPrice() != null ? order.getPrice().amount() : null)
                .orderStatus(order.getOrderStatus())
                .failureMessages(order.getFailureMessages())
                .address(addressDoc)
                .items(itemDocs)
                .build();
    }

    public Order toDomain(OrderDocument document) {
        if (document == null) {
            return null;
        }

        StreetAddress deliveryAddress = null;
        if (document.getAddress() != null) {
            deliveryAddress = new StreetAddress(
                    document.getAddress().getId(),
                    document.getAddress().getStreet(),
                    document.getAddress().getPostalCode(),
                    document.getAddress().getCity()
            );
        }

        List<OrderItem> items = new ArrayList<>();
        if (document.getItems() != null) {
            for (OrderItemDocument itemDoc : document.getItems()) {
                OrderItem item = OrderItem.builder()
                        .id(itemDoc.getId() != null ? new OrderItemId(itemDoc.getId()) : null)
                        .orderId(new OrderId(document.getId()))
                        .product(new Product(new ProductId(itemDoc.getProductId()), null, new Money(itemDoc.getPrice())))
                        .price(new Money(itemDoc.getPrice()))
                        .quantity(itemDoc.getQuantity())
                        .subTotal(new Money(itemDoc.getSubTotal()))
                        .build();
                items.add(item);
            }
        }

        return Order.builder()
                .id(document.getId() != null ? new OrderId(document.getId()) : null)
                .customerId(document.getCustomerId() != null ? new CustomerId(document.getCustomerId()) : null)
                .businessId(document.getBusinessId() != null ? new BusinessId(document.getBusinessId()) : null)
                .trackingId(document.getTrackingId() != null ? new TrackingId(document.getTrackingId()) : null)
                .deliveryAddress(deliveryAddress)
                .price(new Money(document.getPrice()))
                .orderStatus(document.getOrderStatus())
                .items(items)
                .failureMessages(document.getFailureMessages() != null ? new ArrayList<>(document.getFailureMessages()) : new ArrayList<>())
                .build();
    }
}
