package org.example.mypage.activity.domain;

import jakarta.annotation.Nonnull;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import java.time.Instant;


/**
 * 공고 상세 "접속(조회) 기록" 엔티티입니다.
 *
 * <h2>용도</h2>
 * <ul>
 *   <li>사용자가 특정 공고 상세 페이지에 진입(조회)한 사실을 기록합니다.</li>
 *   <li>최근 본 공고, 추천/개인화, 리마인더 대상 산정, CS 분석 등의 근거 데이터로 활용할 수 있습니다.</li>
 * </ul>
 *
 * <h2>데이터 특성</h2>
 * <ul>
 *   <li>1회 접속당 1 row를 저장하는 "로그성 데이터"입니다.</li>
 *   <li>동일 userId/announcementId 조합이 여러 번 저장될 수 있습니다(중복 허용).</li>
 * </ul>
 *
 */
@Entity
@Table(
        name = "outbound",
        indexes = {
                @Index(name = "idx_outbound_user_id", columnList = "user_id")
        }
)
@Getter
@NoArgsConstructor
public class Outbound {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank
    @Column(name = "user_id", nullable = false)
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

    public static Outbound create(@Nonnull String userId, @Nonnull Long announcementId){
        return new Outbound(userId, announcementId);
    }
}
