package org.example.core.mypage.domain;


import jakarta.annotation.Nonnull;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.Instant;


@Entity
@Getter
@Table(
        name = "announcement_bookmark",
        uniqueConstraints = {
                @UniqueConstraint(
                        name = "uk_bookmark_user_announcement",
                        columnNames = {"user_id", "announcement_id"}
                )
        },
        indexes = {
                @Index(name = "idx_bookmark_user_del_created", columnList = "user_id, deleted_at")
        }
)
@NoArgsConstructor
public class AnnouncementBookmark {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank
    @Column(name = "user_id", nullable = false, length = 26)
    private String userId;

    @NotNull
    @Column(name = "announcement_id", nullable = false)
    private Long announcementId;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at", nullable = false)
    private Instant updatedAt;

    @Column(name = "deleted_at")
    private Instant deletedAt;

    public AnnouncementBookmark(@Nonnull String userId, @Nonnull Long announcementId) {
        this.userId = userId;
        this.announcementId = announcementId;
    }
}
