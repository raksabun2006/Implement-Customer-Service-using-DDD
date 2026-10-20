package co.istad.dto;

import java.util.List;

public record PageResponse<T>(
        List<T> content,
        int page,
        int size,
        long totalElements,
        int totalPages,
        boolean first,
        boolean last
) {
    public static <T> PageResponse<T> of(PageResult<T> pageResult) {
        return new PageResponse<>(
                pageResult.content(),
                pageResult.page(),
                pageResult.size(),
                pageResult.totalElements(),
                pageResult.totalPages(),
                pageResult.first(),
                pageResult.last()
        );
    }
}
