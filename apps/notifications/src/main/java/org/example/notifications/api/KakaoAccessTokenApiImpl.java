package org.example.notifications.api;

import lombok.RequiredArgsConstructor;
import org.example.auth.listener.KakaoAccessTokenForNotificationListener;
import org.example.notifications.dto.api.AuthAccessTokenSnapshot;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class KakaoAccessTokenApiImpl implements KakaoAccessTokenApi {

    private final KakaoAccessTokenForNotificationListener tokenListener;

    @Override
    public AuthAccessTokenSnapshot getOrRefreshAccessToken(String userId) {
        // auth쪽 리스너에게 토큰 문자열 받기
        String accessToken = tokenListener.getOrRefreshAccessToken(userId);
        return new AuthAccessTokenSnapshot(accessToken);
    }
}
