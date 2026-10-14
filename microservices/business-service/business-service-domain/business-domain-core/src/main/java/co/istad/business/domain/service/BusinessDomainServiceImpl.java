package co.istad.business.domain.service;

import co.istad.business.domain.entity.Business;
import co.istad.business.domain.event.BusinessRegisteredEvent;
import co.istad.business.domain.event.OrderApprovalEvent;
import co.istad.business.domain.event.OrderApprovedEvent;
import co.istad.business.domain.event.OrderRejectedEvent;
import co.istad.business.domain.valueobject.OrderApprovalStatus;

import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.List;

public class BusinessDomainServiceImpl implements BusinessDomainService {

    private static final String UTC = "UTC";

    @Override
    public BusinessRegisteredEvent validateAndInitiateBusiness(Business business) {
        business.validateBusiness();
        business.initiateBusiness();
        return new BusinessRegisteredEvent(business, ZonedDateTime.now(ZoneId.of(UTC)));
    }

    @Override
    public OrderApprovalEvent validateOrder(Business business, List<String> failureMessages) {
        business.validateOrder(failureMessages);

        if (failureMessages.isEmpty()) {
            business.constructOrderApproval(OrderApprovalStatus.APPROVED);
            return new OrderApprovedEvent(
                    business.getOrderApproval(),
                    business.getId(),
                    failureMessages,
                    ZonedDateTime.now(ZoneId.of(UTC))
            );
        } else {
            business.constructOrderApproval(OrderApprovalStatus.REJECTED);
            return new OrderRejectedEvent(
                    business.getOrderApproval(),
                    business.getId(),
                    failureMessages,
                    ZonedDateTime.now(ZoneId.of(UTC))
            );
        }
    }
}
