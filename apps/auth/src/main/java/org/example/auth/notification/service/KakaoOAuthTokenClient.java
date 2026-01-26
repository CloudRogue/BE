package org.example.auth.notification.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.auth.notification.dto.KakaoTokenResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

//카카오 서버에 리프레쉬토큰으로 엑세스토큰을 갱신 요청
@Slf4j
@Component
@RequiredArgsConstructor
public class KakaoOAuthTokenClient {

    private final RestClient restClient = RestClient.create();

    @Value("${spring.security.oauth2.client.registration.kakao.client-id}")
    private String clientId;

    @Value("${spring.security.oauth2.client.registration.kakao.client-secret}")
    private String clientSecret;

    @Value("${spring.security.oauth2.client.provider.kakao.token-uri}")
    private String tokenUri;

    //리프레쉬토큰으로 엑세스토큰 재발급
    public KakaoTokenResponse refreshAccessToken(String refreshTokenPlain) {
        try {
            return restClient.post()
                    .uri(tokenUri)
                    .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                    .body(java.util.Map.of(
                            "grant_type", "refresh_token",
                            "client_id", clientId,
                            "client_secret", clientSecret,
                            "refresh_token", refreshTokenPlain
                    ))
                    .retrieve()
                    .body(KakaoTokenResponse.class);

        } catch (Exception e) {
            log.error("[KAKAO_TOKEN] refresh failed", e);
            throw new IllegalStateException("Kakao access token refresh failed", e);
        }

    }
}
