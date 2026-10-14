package co.istad.order.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

public record CreateOrderRequest(
        @NotNull(message = "Customer ID is required")
        UUID customerId,

        @NotNull(message = "Business ID is required")
        UUID businessId,

        @NotNull(message = "Price is required")
        @Positive(message = "Price must be greater than zero")
        BigDecimal price,

        @Valid
        @NotNull(message = "Address is required")
        OrderAddressRequest address,

        @Valid
        @NotEmpty(message = "Order must have at least one item")
        List<OrderItemRequest> items
) {
}
