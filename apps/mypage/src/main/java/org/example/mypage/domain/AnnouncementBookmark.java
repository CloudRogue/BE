package org.example.mypage.domain;


import jakarta.annotation.Nonnull;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.Instant;





/**
 * 공고(Announcement)에 대한 사용자 북마크 엔티티입니다. (Hard Delete)
 *
 * <h2>삭제 정책</h2>
 * <ul>
 *   <li>북마크 해제는 row를 물리적으로 삭제(hard delete)합니다.</li>
 *   <li>따라서 "현재 북마크 상태"만 이 테이블이 보장합니다.</li>
 *   <li>이력(추가/해제 기록)이 필요하면 별도 로그/이벤트 테이블(또는 애플리케이션 로그)에 남기는 것을 권장합니다.</li>
 * </ul>
 *
 * <h2>무결성</h2>
 * <ul>
 *   <li>동일 사용자(userId)는 동일 공고(announcementId)를 중복 북마크할 수 없습니다.</li>
 * </ul>
 */
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
                @Index(name = "idx_bookmark_user_created", columnList = "user_id, created_at")
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

    public AnnouncementBookmark(@Nonnull String userId, @Nonnull Long announcementId) {
        this.userId = userId;
        this.announcementId = announcementId;
    }
}
