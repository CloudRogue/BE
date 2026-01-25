package org.example.notifications.repository;

import org.example.notifications.domain.Notification;
import org.example.notifications.domain.NotificationTemplateCode;
import org.springframework.data.jpa.repository.JpaRepository;

public interface NotificationRepository extends JpaRepository<Notification, Long> {


}
