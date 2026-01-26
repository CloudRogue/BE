package org.example.auth.notification.dto;

public record KakaoTokenResponse(
        String token_type,
        String access_token,
        Integer expires_in,
        String refresh_token,
        Integer refresh_token_expires_in
) {}