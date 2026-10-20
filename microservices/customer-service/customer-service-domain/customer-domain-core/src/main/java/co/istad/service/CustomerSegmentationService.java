package co.istad.service;

import co.istad.valueObject.CustomerSegment;

public interface CustomerSegmentationService {

    CustomerSegment getDefaultSegment();

    CustomerSegment evaluateSegment(int completedOrders, double totalSpent);

    boolean canUpgrade(CustomerSegment currentSegment, CustomerSegment targetSegment);
}
