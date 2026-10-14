package co.istad.business.domain.service;

import co.istad.business.domain.entity.Business;
import co.istad.business.domain.event.BusinessRegisteredEvent;
import co.istad.business.domain.event.OrderApprovalEvent;

import java.util.List;

public interface BusinessDomainService {

    BusinessRegisteredEvent validateAndInitiateBusiness(Business business);

    OrderApprovalEvent validateOrder(Business business, List<String> failureMessages);
}
