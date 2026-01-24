package org.example.notifications.repository;

import org.example.notifications.domain.NotificationButton;
import org.springframework.data.jpa.repository.JpaRepository;

public interface NotificationButtonRepository extends JpaRepository<NotificationButton, Long> {
}
