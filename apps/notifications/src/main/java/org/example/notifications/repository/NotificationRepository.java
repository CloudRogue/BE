package org.example.notifications.repository;

import org.example.notifications.domain.Notification;
import org.example.notifications.domain.NotificationTemplateCode;
import org.springframework.data.jpa.repository.JpaRepository;

public interface NotificationRepository extends JpaRepository<Notification, Long> {

    // 같은 유저에게 같은 알림을 중복 생성하지 않기
    boolean existsByUserIdAndTemplateCodeAndAnnouncementId(
            String userId,
            NotificationTemplateCode templateCode,
            Long announcementId
    );
}
