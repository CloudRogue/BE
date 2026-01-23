package org.example.notifications.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;

@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
@Table(name = "notification_preferences")
public class NotificationPreference {

    @Id
    @Column(name = "user_id", nullable = false)
    private String userId;

    @Column(name = "allowed", nullable = false) // 알림 허용 여부
    private boolean allowed;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    // 정적 생성 메서드
    public static NotificationPreference create(String userId, boolean allowed) {
        NotificationPreference p = new NotificationPreference();
        p.userId = userId;
        p.allowed = allowed;
        return p;
    }

    // 도메인 변경 메서드
    public void changeAllowed(boolean allowed) {
        this.allowed = allowed; // 알림 허용 여부 변경
    }


}
