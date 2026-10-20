package co.istad.dto;

import co.istad.valueObject.CustomerSegment;
import co.istad.valueObject.CustomerStatus;

import java.time.Instant;
import java.util.Map;

public record CustomerStatisticResult(
        long totalCustomers,
        long activeCustomers,
        long inactiveCustomers,
        long silverCustomers,
        long goldCustomers,
        long premiumCustomers,
        Map<CustomerStatus, Long> customersByStatus,
        Map<CustomerSegment, Long> customersBySegment,
        Instant generatedAt
) {
}
