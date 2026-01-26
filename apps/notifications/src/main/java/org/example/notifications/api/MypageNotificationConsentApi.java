package org.example.notifications.api;

import java.util.List;

//카카오 동의한 유저가져오기
public interface MypageNotificationConsentApi {
    List<String> findKakaoAllowedUserIds();
}
