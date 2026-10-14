package co.istad.business.domain.entity;

import co.istad.business.domain.valueobject.BusinessId;
import co.istad.business.domain.valueobject.OrderApprovalId;
import co.istad.business.domain.valueobject.OrderApprovalStatus;
import co.istad.business.domain.valueobject.OrderId;
import co.istad.entity.BaseEntity;

public class OrderApproval extends BaseEntity<OrderApprovalId> {
    private final BusinessId businessId;
    private final OrderId orderId;
    private final OrderApprovalStatus orderApprovalStatus;

    public OrderApproval(OrderApprovalId orderApprovalId, BusinessId businessId, OrderId orderId, OrderApprovalStatus orderApprovalStatus) {
        super.setId(orderApprovalId);
        this.businessId = businessId;
        this.orderId = orderId;
        this.orderApprovalStatus = orderApprovalStatus;
    }

    public BusinessId getBusinessId() {
        return businessId;
    }

    public OrderId getOrderId() {
        return orderId;
    }

    public OrderApprovalStatus getOrderApprovalStatus() {
        return orderApprovalStatus;
    }
}
