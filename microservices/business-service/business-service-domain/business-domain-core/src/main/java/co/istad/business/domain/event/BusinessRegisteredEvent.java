package co.istad.business.domain.event;

import co.istad.business.domain.entity.Business;
import co.istad.event.DomainEvent;

import java.time.ZonedDateTime;

public class BusinessRegisteredEvent implements DomainEvent<Business> {
    private final Business business;
    private final ZonedDateTime createdAt;

    public BusinessRegisteredEvent(Business business, ZonedDateTime createdAt) {
        this.business = business;
        this.createdAt = createdAt;
    }

    public Business getBusiness() {
        return business;
    }

    public ZonedDateTime getCreatedAt() {
        return createdAt;
    }
}
