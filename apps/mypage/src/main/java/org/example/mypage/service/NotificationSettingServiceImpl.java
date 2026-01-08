package org.example.mypage.service;


import lombok.RequiredArgsConstructor;
import org.example.mypage.domain.NotificationSetting;
import org.example.mypage.dto.EmailEnabledView;
import org.example.mypage.dto.KakaoEnabledView;
import org.example.mypage.dto.request.ReminderSettingUpsertRequest;
import org.example.mypage.dto.response.ReminderSettingResponse;
import org.example.mypage.exception.NotificationChannelDisabledException;
import org.example.mypage.repository.NotificationRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class NotificationSettingServiceImpl implements NotificationSettingService{
    private final NotificationRepository notificationRepository;


    @Transactional(readOnly = true)
    public boolean getKakaoEnabled(String userId) {
        return notificationRepository.findKakaoEnabledByUserId(userId)
                .map(KakaoEnabledView::isKakaoEnabled)
                .orElse(false);
    }

    @Override
    public void updateKakaoEnabled(String userId, boolean enabled) {
        NotificationSetting setting = notificationRepository.findByUserId(userId)
                .orElseGet(() -> NotificationSetting.createDefault(userId));

        setting.setKakaoEnabled(enabled);
        notificationRepository.save(setting);
    }

    @Override
    public boolean getEmailEnabled(String userId) {
        return notificationRepository.findEmailEnabledByUserId(userId)
                .map(EmailEnabledView::isEmailEnabled)
                .orElse(false);
    }

    @Override
    public void updateEmailEnabled(String userId, boolean enabled) {
        NotificationSetting setting = notificationRepository.findByUserId(userId)
                .orElseGet(() -> NotificationSetting.createDefault(userId));

        setting.setEmailEnabled(enabled);
        notificationRepository.save(setting);
    }


    @Override
    @Transactional(readOnly = true)
    public ReminderSettingResponse getReminderSetting(String userId) {
        return notificationRepository.findByUserId(userId)
                .map(s -> ReminderSettingResponse.from(
                        s.getSendAtHour(),
                        s.getReminderDaysBefore()
                ))

                .orElseGet(() -> ReminderSettingResponse.from(null, null));
    }

    @Override
    @Transactional
    public void upsertReminderSetting(String userId, ReminderSettingUpsertRequest req) {
        NotificationSetting s = notificationRepository.findByUserId(userId)
                .orElseGet(() -> NotificationSetting.createDefault(userId));

        if (!s.isEmailEnabled() && !s.isKakaoEnabled()) {
            throw new NotificationChannelDisabledException();
        }

        Integer sendAtHour = req.sendAtHour();
        Integer daysBefore = req.daysBefore();

        if (sendAtHour == null || daysBefore == null) {
            s.updateReminder(null, null);
            notificationRepository.save(s);
            return;
        }

        s.updateReminder(sendAtHour, daysBefore);
        notificationRepository.save(s);

    }
}

