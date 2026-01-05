package org.example.core.mypage.domain;

import jakarta.annotation.Nonnull;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.Instant;

@Entity
@Getter
@NoArgsConstructor
@Table(
        name = "notification_setting",
        indexes = {
                @Index(name = "idx_notification_setting_user_id", columnList = "user_id")
        }
)
public class NotificationSetting {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank
    @Column(name = "user_id", nullable = false, unique = true, length = 26)
    private String userId;

    @Column(name = "kakao_enabled", nullable = false)
    private boolean kakaoEnabled;

    @Column(name = "email_enabled", nullable = false)
    private boolean emailEnabled;

    @Column(name = "reminder_days_before", nullable = false)
    private int reminderDaysBefore;

    @Column(name = "reminder_hours_before", nullable = false)
    private int reminderHoursBefore;

    @UpdateTimestamp
    @Column(name = "updated_at", nullable = false)
    private Instant updatedAt;

    public NotificationSetting(@Nonnull String userId, boolean kakaoEnabled, boolean emailEnabled, int reminderDaysBefore, int reminderHoursBefore) {
        this.userId = userId;
        this.kakaoEnabled = kakaoEnabled;
        this.emailEnabled = emailEnabled;
        this.reminderDaysBefore = reminderDaysBefore;
        this.reminderHoursBefore = reminderHoursBefore;
    }
}
