package org.example.core.community.dto.response;

import org.example.core.community.domain.Comment;

import java.time.OffsetDateTime;

public record CommentContentResponse(
        Long id,
        String announcementId,
        Long parentId,
        String content,
        boolean isDeleted,
        OffsetDateTime createdAt,
        OffsetDateTime updatedAt,
        String authorUserId
) {
    public static CommentContentResponse of(
            Comment c
    ) {
        return new CommentContentResponse(
                c.getId(),
                c.getAnnouncementId(),
                c.getParent() != null ? c.getParent().getId() : null,
                c.getDeletedAt() != null ? "삭제된 댓글입니다." : c.getContent(),
                c.getDeletedAt() != null,
                c.getCreatedAt(),
                c.getUpdatedAt(),
                c.getAuthorUserId()
        );
    }
}