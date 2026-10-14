package co.istad.business.domain.entity;

import co.istad.Money;
import co.istad.entity.BaseEntity;
import co.istad.business.domain.valueobject.ProductId;

public class Product extends BaseEntity<ProductId> {
    private String name;
    private Money price;
    private int quantity;
    private boolean available;

    public Product(ProductId productId, String name, Money price, int quantity, boolean available) {
        super.setId(productId);
        this.name = name;
        this.price = price;
        this.quantity = quantity;
        this.available = available;
    }

    public Product(ProductId productId, String name, Money price, boolean available) {
        this(productId, name, price, 0, available);
    }

    public Product(ProductId productId) {
        super.setId(productId);
    }

    public void updateWithConfirmedNameAndPrice(String name, Money price) {
        this.name = name;
        this.price = price;
    }

    public String getName() {
        return name;
    }

    public Money getPrice() {
        return price;
    }

    public int getQuantity() {
        return quantity;
    }

    public boolean isAvailable() {
        return available;
    }

    public void setAvailable(boolean available) {
        this.available = available;
    }
}
