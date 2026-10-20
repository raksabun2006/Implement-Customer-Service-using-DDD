package co.istad.service;

import co.istad.valueObject.CustomerSegment;

public class CustomerSegmentationServiceImpl implements CustomerSegmentationService {

    // Configurable thresholds for tier qualification
    private final double goldSpendingThreshold;
    private final int goldOrderThreshold;
    private final double premiumSpendingThreshold;
    private final int premiumOrderThreshold;

    public CustomerSegmentationServiceImpl() {
        this(500.0, 10, 2000.0, 50);
    }

    public CustomerSegmentationServiceImpl(double goldSpendingThreshold,
                                          int goldOrderThreshold,
                                          double premiumSpendingThreshold,
                                          int premiumOrderThreshold) {
        this.goldSpendingThreshold = goldSpendingThreshold;
        this.goldOrderThreshold = goldOrderThreshold;
        this.premiumSpendingThreshold = premiumSpendingThreshold;
        this.premiumOrderThreshold = premiumOrderThreshold;
    }

    @Override
    public CustomerSegment getDefaultSegment() {
        return CustomerSegment.SILVER;
    }

    @Override
    public CustomerSegment evaluateSegment(int completedOrders, double totalSpent) {
        if (totalSpent >= premiumSpendingThreshold || completedOrders >= premiumOrderThreshold) {
            return CustomerSegment.PREMIUM;
        }
        if (totalSpent >= goldSpendingThreshold || completedOrders >= goldOrderThreshold) {
            return CustomerSegment.GOLD;
        }
        return CustomerSegment.SILVER;
    }

    @Override
    public boolean canUpgrade(CustomerSegment currentSegment, CustomerSegment targetSegment) {
        if (currentSegment == null || targetSegment == null) {
            return false;
        }
        return targetSegment.ordinal() >= currentSegment.ordinal();
    }
}
