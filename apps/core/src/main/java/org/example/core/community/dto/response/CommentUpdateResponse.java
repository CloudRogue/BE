package org.example.core.community.dto.response;

import java.time.OffsetDateTime;

public record CommentUpdateResponse(
        Long commentId,
        OffsetDateTime updatedAt
) {
    public static CommentUpdateResponse of(Long commentId, OffsetDateTime updatedAt) {
        return new CommentUpdateResponse(
                commentId,
                updatedAt
        );
    }
}
