package co.istad.business.domain.entity;

import co.istad.business.domain.exception.BusinessDomainException;
import co.istad.business.domain.valueobject.*;
import co.istad.entity.AggregateRoot;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class Business extends AggregateRoot<BusinessId> {
    private String name;
    private BusinessContact contact;
    private boolean active;
    private List<Product> products;
    private OrderApproval orderApproval;
    private OrderDetail orderDetail;

    public void validateBusiness() {
        if (name == null || name.trim().isEmpty()) {
            throw new BusinessDomainException("Business name must not be empty!");
        }
        if (contact == null) {
            throw new BusinessDomainException("Business contact must not be null!");
        }
    }

    public void initiateBusiness() {
        if (getId() == null) {
            super.setId(new BusinessId(UUID.randomUUID()));
        }
        this.active = true;
        if (this.products == null) {
            this.products = new ArrayList<>();
        }
    }

    public void validateOrder(List<String> failureMessages) {
        if (!active) {
            failureMessages.add("Business is currently not active!");
        }
        if (orderDetail == null || orderDetail.getProducts() == null) {
            failureMessages.add("Order detail products cannot be null!");
            return;
        }

        for (Product orderProduct : orderDetail.getProducts()) {
            Product currentProduct = products.stream()
                    .filter(p -> p.getId().equals(orderProduct.getId()))
                    .findFirst()
                    .orElse(null);

            if (currentProduct == null) {
                failureMessages.add("Product with id " + orderProduct.getId().value() + " is not available in business!");
            } else if (!currentProduct.isAvailable()) {
                failureMessages.add("Product " + currentProduct.getName() + " is currently unavailable!");
            } else if (!currentProduct.getPrice().equals(orderProduct.getPrice())) {
                failureMessages.add("Product price has changed for: " + currentProduct.getName());
            }
        }
    }

    public void constructOrderApproval(OrderApprovalStatus orderApprovalStatus) {
        if (orderDetail == null || orderDetail.getOrderId() == null) {
            throw new BusinessDomainException("Cannot construct order approval without order detail!");
        }
        this.orderApproval = new OrderApproval(
                new OrderApprovalId(UUID.randomUUID()),
                super.getId(),
                orderDetail.getOrderId(),
                orderApprovalStatus
        );
    }

    public void updateBusinessInfo(String name, BusinessContact contact) {
        if (name != null && !name.trim().isEmpty()) {
            this.name = name;
        }
        if (contact != null) {
            this.contact = contact;
        }
    }

    public void addProduct(Product product) {
        if (this.products == null) {
            this.products = new ArrayList<>();
        }
        this.products.add(product);
    }

    private Business(Builder builder) {
        super.setId(builder.id);
        name = builder.name;
        contact = builder.contact;
        active = builder.active;
        products = builder.products;
        orderApproval = builder.orderApproval;
        orderDetail = builder.orderDetail;
    }

    public static Builder builder() {
        return new Builder();
    }

    public String getName() {
        return name;
    }

    public BusinessContact getContact() {
        return contact;
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    public List<Product> getProducts() {
        return products;
    }

    public OrderApproval getOrderApproval() {
        return orderApproval;
    }

    public OrderDetail getOrderDetail() {
        return orderDetail;
    }

    public void setOrderDetail(OrderDetail orderDetail) {
        this.orderDetail = orderDetail;
    }

    public static final class Builder {
        private BusinessId id;
        private String name;
        private BusinessContact contact;
        private boolean active;
        private List<Product> products = new ArrayList<>();
        private OrderApproval orderApproval;
        private OrderDetail orderDetail;

        private Builder() {
        }

        public Builder id(BusinessId val) {
            id = val;
            return this;
        }

        public Builder name(String val) {
            name = val;
            return this;
        }

        public Builder contact(BusinessContact val) {
            contact = val;
            return this;
        }

        public Builder active(boolean val) {
            active = val;
            return this;
        }

        public Builder products(List<Product> val) {
            products = val;
            return this;
        }

        public Builder orderApproval(OrderApproval val) {
            orderApproval = val;
            return this;
        }

        public Builder orderDetail(OrderDetail val) {
            orderDetail = val;
            return this;
        }

        public Business build() {
            return new Business(this);
        }
    }
}
