package org.example.mypage.service;


import org.example.mypage.dto.request.ReminderSettingUpsertRequest;
import org.example.mypage.dto.response.ReminderSettingResponse;

public interface NotificationSettingService {
    boolean getKakaoEnabled(String userId);
    void updateKakaoEnabled(String userId, boolean enabled);

    boolean getEmailEnabled(String userId);
    void updateEmailEnabled(String userId, boolean enabled);

    ReminderSettingResponse getReminderSetting(String userId);
    void upsertReminderSetting(String userId, ReminderSettingUpsertRequest req);
}
