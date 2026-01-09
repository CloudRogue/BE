package org.example.core.community.domain;

import jakarta.persistence.*;
import org.example.core.community.domain.enums.ContentFilter;

import java.time.OffsetDateTime;

@Entity
@Table(
        name = "comment_moderation",
        indexes = {
                @Index(name = "idx_cm_comment", columnList = "comment_id"),
                @Index(name = "idx_cm_created", columnList = "created_at")
        }
)
public class CommentModeration {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "comment_id", nullable = false)
    private Comment comment;

    @Enumerated(EnumType.STRING)
    @Column(name = "result", nullable = false, length = 32)
    private ContentFilter result;

    @Column(name = "created_at", nullable = false)
    private OffsetDateTime createdAt;

}
