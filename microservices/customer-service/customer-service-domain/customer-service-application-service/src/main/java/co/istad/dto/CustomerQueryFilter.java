package co.istad.dto;

import co.istad.valueObject.CustomerSegment;
import co.istad.valueObject.CustomerStatus;

import java.time.Instant;

public record CustomerQueryFilter(
        String search,
        CustomerStatus status,
        CustomerSegment segment,
        Instant fromDate,
        Instant toDate,
        int page,
        int size,
        String sortBy,
        String sortDirection
) {
    public CustomerQueryFilter {
        if (page < 0) page = 0;
        if (size <= 0) size = 10;
        if (sortBy == null || sortBy.isBlank()) sortBy = "createdAt";
        if (sortDirection == null || sortDirection.isBlank()) sortDirection = "desc";
    }

    public static CustomerQueryFilter of(
            String search,
            CustomerStatus status,
            CustomerSegment segment,
            Instant fromDate,
            Instant toDate,
            Integer page,
            Integer size,
            String sortBy,
            String sortDirection
    ) {
        return new CustomerQueryFilter(
                search,
                status,
                segment,
                fromDate,
                toDate,
                page != null ? page : 0,
                size != null ? size : 10,
                sortBy != null ? sortBy : "createdAt",
                sortDirection != null ? sortDirection : "desc"
        );
    }
}
