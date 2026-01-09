package org.example.core.community.dto.response;

import org.example.core.community.domain.Comment;

import java.time.OffsetDateTime;

public record CommentListResponse(
        Long id,
        String announcementId,
        String authorUserId,
        Long parentId,
        String content,
        Long likeCount,
        Long reportCount,
        boolean isDeleted,
        OffsetDateTime createdAt,
        OffsetDateTime updatedAt
) {
    public static CommentListResponse of(Comment c, Long likeCount, Long reportCount) {
        return new CommentListResponse(
                c.getId(),
                c.getAnnouncementId(),
                c.getAuthorUserId(),
                c.getParent() != null ? c.getParent().getId() : null,
                c.getDeletedAt() != null ? "삭제된 댓글입니다." : c.getContent(),
                likeCount,
                reportCount,
                c.getDeletedAt() != null,
                c.getCreatedAt(),
                c.getUpdatedAt()
        );
    }
}