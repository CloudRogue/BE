package org.example.notifications.domain;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity // 알림 엔티티
@Table(
        name = "notifications",
        indexes = {
                // 유저별 알림 조회용으로
                @Index(name = "idx_notif_user_created", columnList = "user_id, created_at"),
                // 중복 발송 방지검사하기용
                @Index(name = "idx_notif_fire_date", columnList = "fire_date")
        }
)
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Notification {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "user_id", nullable = false)
    private String userId;

    @Enumerated(EnumType.STRING)
    @Column(name = "template_code", nullable = false)
    private NotificationTemplateCode templateCode;

    @Column(name = "announcement_id")
    private Long announcementId;

    //메인 문구
    @Column(name = "title", length = 200, nullable = false)
    private String title;

    //서브 문구
    @Column(name = "body", length = 500, nullable = false)
    private String body;

    //중복 방지용
    @Column(name = "fire_date", nullable = false)
    private LocalDate fireDate;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;


    // 정적 생성 팩토리
    public static Notification create(
            String userId,
            NotificationTemplateCode templateCode,
            Long announcementId,
            String title,
            String body,
            LocalDate fireDate
    ) {
        Notification n = new Notification();
        n.userId = userId;
        n.templateCode = templateCode;
        n.announcementId = announcementId;
        n.title = title;
        n.body = body;
        n.fireDate = fireDate;
        return n;
    }
}
