package org.example.auth.listener;

import lombok.RequiredArgsConstructor;
import org.example.auth.notification.service.KakaoAccessTokenService;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class KakaoAccessTokenForNotificationListenerImpl implements KakaoAccessTokenForNotificationListener {

    private final KakaoAccessTokenService kakaoAccessTokenService;

    @Override
    public String getOrRefreshAccessToken(String userId) {
        return kakaoAccessTokenService.getOrRefresh(userId);
    }
}
