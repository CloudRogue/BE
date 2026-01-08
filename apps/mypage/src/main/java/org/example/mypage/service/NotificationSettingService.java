package org.example.mypage.service;

public interface NotificationSettingService {
    boolean getKakaoEnabled(String userId);
    void updateKakaoEnabled(String userId, boolean enabled);
}
