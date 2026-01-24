package org.example.notifications.repository;

import org.example.notifications.domain.NotificationPreference;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface NotificationPreferenceRepository extends JpaRepository<NotificationPreference, String> {

    // 알림 허용한 유저 전체 대상
    List<NotificationPreference> findAllByAllowedTrue();
}
