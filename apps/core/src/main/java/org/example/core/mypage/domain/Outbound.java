package org.example.core.mypage.domain;

import jakarta.annotation.Nonnull;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import java.time.Instant;


@Entity
@Table(
        name = "outbound",
        indexes = {
                @Index(name = "idx_outbound_user_id", columnList = "user_id")
        }
)
@NoArgsConstructor
public class Outbound {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank
    @Column(name = "user_id", nullable = false, length = 24)
    private String userId;

    @NotNull
    @Column(name = "announcement_id", nullable = false)
    private Long announcementId;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;


    public Outbound(@Nonnull String userId, @Nonnull Long announcementId) {
        this.userId = userId;
        this.announcementId = announcementId;
    }
}
