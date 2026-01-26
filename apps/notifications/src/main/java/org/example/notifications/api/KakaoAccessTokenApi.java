package org.example.notifications.api;

import org.example.notifications.dto.api.AuthAccessTokenSnapshot;

public interface KakaoAccessTokenApi {
    AuthAccessTokenSnapshot getOrRefreshAccessToken(String userId);
}