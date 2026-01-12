package org.example.core.community.domain;

import jakarta.persistence.*;
import jakarta.validation.constraints.Size;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.example.core.community.domain.enums.ContentFilter;
import org.example.core.community.exception.BlankContentException;

import java.time.OffsetDateTime;


@Entity
@Getter
@Table(
        name = "community_comment",
        indexes = {
                @Index(name = "idx_comment_announcement_created", columnList = "announcement_id, created_at"),
                @Index(name = "idx_comment_announcement_parent", columnList = "announcement_id, parent_id"),
                @Index(name = "idx_comment_author", columnList = "author_user_id")
        }
)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Comment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "announcement_id", nullable = false, length = 64)
    private String announcementId;

    @Column(name = "author_user_id", nullable = false, length = 26)
    private String authorUserId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "parent_id")
    private Comment parent;

    @Size(max = 500)
    @Column(name = "content", nullable = false, length = 500)
    private String content;

    @Enumerated(EnumType.STRING)
    @Column(name = "content_filter", nullable = false, length = 32)
    private ContentFilter contentFilter = ContentFilter.NONE;

    @Column(name = "deleted_at")
    private OffsetDateTime deletedAt;

    @Column(name = "created_at", nullable = false)
    private OffsetDateTime createdAt;

    @Column(name = "updated_at")
    private OffsetDateTime updatedAt;


    public static Comment newParentComment(String announcementId, String authorUserId, String content) {
        Comment c = new Comment();
        c.announcementId = announcementId;
        c.parent = null;
        c.authorUserId = authorUserId;
        c.content = content;
        c.contentFilter = ContentFilter.NONE;
        c.createdAt = OffsetDateTime.now();
        return c;
    }

    public static Comment newKindAnswer(String announcementId, Comment parentQuestion,
                                             String authorUserId, String content) {
        Comment c = new Comment();
        c.announcementId = announcementId;
        c.parent = parentQuestion;
        c.authorUserId = authorUserId;
        c.content = content;
        c.contentFilter = ContentFilter.NONE;
        c.createdAt = OffsetDateTime.now();
        return c;
    }

    public void markSoftDeleted() {
        if (this.deletedAt != null) {
            this.deletedAt = OffsetDateTime.now();
        }
    }

    public void updateContent(String newContent) {
        if(newContent == null || newContent.isBlank()) {
            throw new BlankContentException("댓글 내용은 비어 있을 수 없습니다.");
        }
        this.content = newContent;
    }

    public void touchUpdatedAt() {
        this.updatedAt = OffsetDateTime.now();
    }
}
