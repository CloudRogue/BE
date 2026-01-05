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

    // FK를 진짜로 걸지/안 걸지는 선택인데,
    // JPA 상으론 연관으로 두되(편의), 운영 DB 제약은 팀 정책대로
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "comment_id", nullable = false)
    private Comment comment;

    @Enumerated(EnumType.STRING)
    @Column(name = "result", nullable = false, length = 32)
    private ContentFilter result; // NONE/PROFANITY/...

    @Column(name = "score")
    private Integer score; // 선택: 0~100 같은 점수


    @Column(name = "created_at", nullable = false)
    private OffsetDateTime createdAt;

    // getters/setters ...
}
