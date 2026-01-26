package org.example.mypage.notify.service.internal;

import lombok.RequiredArgsConstructor;
import org.example.mypage.notify.repository.NotificationRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class InternalNotificationConsentServiceImpl implements InternalNotificationConsentService {

    private final NotificationRepository notificationRepository;

    @Override
    @Transactional(readOnly = true)
    public List<String> getKakaoAllowedUserIds() {
        return notificationRepository.findAllUserIdsByKakaoEnabledTrue();
    }
}
