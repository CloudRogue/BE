package org.example.core.community.domain;

import jakarta.persistence.*;
import org.example.core.community.domain.enums.ReportReasonType;

import java.time.OffsetDateTime;

@Entity
@Table(
        name = "community_comment_report",
        uniqueConstraints = {
                @UniqueConstraint(name = "uk_report_comment_user", columnNames = {"comment_id", "user_id"})
        },
        indexes = {
                @Index(name = "idx_report_comment", columnList = "comment_id"),
                @Index(name = "idx_report_user", columnList = "user_id")
        }
)
public class CommentReport {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "comment_id", nullable = false)
    private Comment comment;

    @Column(name = "user_id", nullable = false, length = 26)
    private String userId;

    @Enumerated(EnumType.STRING)
    @Column(name = "reason_type", nullable = false, length = 32)
    private ReportReasonType reasonType;

    @Column(name = "reason", length = 500)
    private String reason;

    @Column(name = "created_at", nullable = false)
    private OffsetDateTime createdAt;

    protected CommentReport() {}

    public static CommentReport of(Comment comment, String userId, ReportReasonType reasonType, String reason) {
        CommentReport r = new CommentReport();
        r.comment = comment;
        r.userId = userId;
        r.reasonType = reasonType;
        r.reason = reason;
        r.createdAt = OffsetDateTime.now();
        return r;
    }

}
