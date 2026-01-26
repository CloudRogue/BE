package org.example.mypage.notify.service.internal;

import java.util.List;

public interface InternalNotificationConsentService {

    //카카오 동의 유저만 반환
    List<String> getKakaoAllowedUserIds();
}
