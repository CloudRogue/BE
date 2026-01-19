package org.example.core.community.dto.response;

import java.util.List;

public record CommentSliceResponse(
        List<CommentContentResponse> items,
        Meta meta
) {
    // 서비스에서 호출할 생성자
    public CommentSliceResponse(
            List<CommentContentResponse> items,
            Long nextCursor,
            boolean hasNext,
            boolean hasPrev,
            long totalElements,
            int size
    ) {
        this(items, new Meta(
                nextCursor,
                hasNext,
                hasPrev,
                totalElements,
                size
        ));
    }

    public record Meta(
            int page,
            int size,
            long totalElements,
            int totalPages,
            Long nextCursor,
            boolean hasNext,
            boolean hasPrev
    ) {
        public Meta(Long nextCursor, boolean hasNext, boolean hasPrev, long totalElements, int size) {
            this(
                    0,
                    20,
                    totalElements,
                    (int) Math.ceil((double) totalElements / size),
                    nextCursor,
                    hasNext,
                    hasPrev
            );
        }
    }
}
