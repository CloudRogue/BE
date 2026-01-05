package org.example.core.community.domain;

import jakarta.persistence.*;

import java.time.OffsetDateTime;

@Entity
@Table(
        name = "community_comment_like",
        uniqueConstraints = {
                @UniqueConstraint(name = "uk_like_comment_user", columnNames = {"comment_id", "user_id"})
        },
        indexes = {
                @Index(name = "idx_like_comment", columnList = "comment_id"),
                @Index(name = "idx_like_user", columnList = "user_id")
        }
)
public class CommentLike {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "comment_id", nullable = false)
    private Comment comment;

    @Column(name = "user_id", nullable = false, length = 26)
    private String userId;

    @Column(name = "created_at", nullable = false)
    private OffsetDateTime createdAt;

    protected CommentLike() {}

    public static CommentLike of(Comment comment, String userId) {
        CommentLike like = new CommentLike();
        like.comment = comment;
        like.userId = userId;
        like.createdAt = OffsetDateTime.now();
        return like;
    }

}
