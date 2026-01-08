package org.example.mypage.repository;

import org.example.mypage.domain.NotificationSetting;
import org.example.mypage.dto.EmailEnabledView;
import org.example.mypage.dto.KakaoEnabledView;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface NotificationRepository extends JpaRepository<NotificationSetting, Long> {
    Optional<NotificationSetting> findByUserId(String userId);
    Optional<KakaoEnabledView> findKakaoEnabledByUserId(String userId);
    Optional<EmailEnabledView> findEmailEnabledByUserId(String userId);
}
