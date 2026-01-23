package org.example.notifications.domain;


import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity // 알림 버튼 엔티티
@Table(
        name = "notification_buttons",
        indexes = {
                // 알림 기준 버튼 조회
                @Index(name = "idx_btn_notification", columnList = "notification_id")
        }
)
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class NotificationButton {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "notification_id", nullable = false)
    private Notification notification;

    //버튼 이름
    @Column(name = "name", length = 60, nullable = false)
    private String name;

    //이동 url
    @Column(name = "url", length = 1000, nullable = false)
    private String url;

    //버튼 순서
    @Column(name = "sort_order", nullable = false)
    private int sortOrder;


    // 정적 생성 메소드
    public static NotificationButton create(
            Notification notification,
            String name,
            String url,
            int sortOrder
    ) {
        NotificationButton b = new NotificationButton();
        b.notification = notification;
        b.name = name;
        b.url = url;
        b.sortOrder = sortOrder;
        return b;
    }
}
