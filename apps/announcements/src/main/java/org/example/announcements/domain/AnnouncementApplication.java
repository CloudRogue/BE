package org.example.announcements.domain;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(
        name = "announcement_applications",
        uniqueConstraints = {
                //같은 유저가 같은공고에 중복기록 방지
                @UniqueConstraint(
                        name = "uq_application_user_announcement",
                        columnNames = {"user_id", "announcement_id"}
                )
        },
        indexes = {
                @Index(
                        name = "idx_application_user_announcement",
                        columnList = "user_id, announcement_id"
                )
        }
)
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class AnnouncementApplication {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    //유저 아이디
    @Column(name = "user_id", nullable = false, length = 64)
    private String userId;

    //공고 아이디
    @Column(name = "announcement_id", nullable = false)
    private Long announcementId;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    //정적 팩토리 메소드
    public static AnnouncementApplication create(String userId, Long announcementId) {
        AnnouncementApplication app = new AnnouncementApplication();
        app.userId = userId;
        app.announcementId = announcementId;
        return app;
    }
}
