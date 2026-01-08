package org.example.mypage.domain;

import jakarta.annotation.Nonnull;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.Instant;

/**
 * 사용자 알림 설정 엔티티입니다.
 *
 * <h2>개요</h2>
 * <ul>
 *   <li>사용자 1명당 1개의 설정 row를 가집니다. ({@link #userId} 유니크)</li>
 *   <li>알림 채널별 on/off 및 리마인더(사전 알림) 시점을 저장합니다.</li>
 * </ul>
 *
 * <h2>DB 제약/인덱스</h2>
 * <ul>
 *   <li>user_id는 unique=true로 사용자당 1 row를 보장합니다.</li>
 *   <li>idx_notification_setting_user_id 인덱스가 추가되어 있습니다.
 *       (단, user_id가 유니크라면 유니크 인덱스가 이미 생기므로 중복 인덱스가 될 수 있습니다.)</li>
 * </ul>
 */
@Entity
@Getter
@NoArgsConstructor
@Table(name = "notification_setting")
public class NotificationSetting {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank
    @Column(name = "user_id", nullable = false, unique = true, length = 26)
    private String userId;

    @Setter
    @Column(name = "kakao_enabled", nullable = false)
    private boolean kakaoEnabled;

    @Setter
    @Column(name = "email_enabled", nullable = false)
    private boolean emailEnabled;

    @Column(name = "reminder_days_before", nullable = false)
    private Integer reminderDaysBefore;

    @Column(name = "send_at_hour", nullable = false)
    private Integer sendAtHour;

    @UpdateTimestamp
    @Column(name = "updated_at", nullable = false)
    private Instant updatedAt;

    public NotificationSetting(
            @Nonnull String userId,
            boolean kakaoEnabled,
            boolean emailEnabled,
            Integer reminderDaysBefore,
            Integer sendAtHour
    ) {
        this.userId = userId;
        this.kakaoEnabled = kakaoEnabled;
        this.emailEnabled = emailEnabled;
        this.reminderDaysBefore = reminderDaysBefore;
        this.sendAtHour = sendAtHour;
    }

    public void updateReminder(Integer sendAtHour, Integer reminderDaysBefore){
        this.sendAtHour = sendAtHour;
        this.reminderDaysBefore = reminderDaysBefore;
    }

    public static NotificationSetting createDefault(String userId) {
        NotificationSetting s = new NotificationSetting();
        s.userId = userId;
        s.reminderDaysBefore = 0;
        s.sendAtHour = 0;
        s.emailEnabled = false;
        s.kakaoEnabled = false;
        return s;
    }

}
