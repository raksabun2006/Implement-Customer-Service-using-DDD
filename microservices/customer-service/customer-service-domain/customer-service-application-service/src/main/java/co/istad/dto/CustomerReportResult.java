package co.istad.dto;

import co.istad.valueObject.CustomerSegment;
import co.istad.valueObject.CustomerStatus;

import java.util.Map;

public record CustomerReportResult(
        long totalCustomers,
        Map<CustomerStatus, Long> countByStatus,
        Map<CustomerSegment, Long> countBySegment,
        Map<String, Long> countByStatusAndSegment
) {
}
