package org.example.auth.notification.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.auth.notification.dto.KakaoTokenResponse;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Slf4j
@Service
@RequiredArgsConstructor
public class CachedAccessTokenService implements KakaoAccessTokenService {

    private final KakaoAccessTokenCache accessTokenCache;
    private final KakaoRefreshTokenStore refreshTokenStore;
    private final KakaoOAuthTokenClient tokenClient;

    @Override
    public String getOrRefresh(String userId) {
        //히트다?? 바로반환
        String cached = accessTokenCache.get(userId);
        if (cached != null) {
            return cached;
        }

        String refreshTokenPlain = refreshTokenStore.getRequiredPlain(userId);

        //재발급 요청 리프레쉬토큰으로
        KakaoTokenResponse res =  tokenClient.refreshAccessToken(refreshTokenPlain);


        //필수값 검증
        String accessToken = res.access_token();
        Integer expiresIn = res.expires_in();

        if (accessToken == null || expiresIn == null) {
            throw new IllegalStateException("Invalid Kakao token response (missing access_token/expires_in)");
        }

        //레디스에 저장
        accessTokenCache.set(userId, accessToken, expiresIn.longValue());


        //리프레쉬토큰 로테이션 필요하면 교체
        if (res.refresh_token() != null) {

            LocalDateTime refreshExpiresAt = null;
            if (res.refresh_token_expires_in() != null) {
                refreshExpiresAt = LocalDateTime.now().plusSeconds(res.refresh_token_expires_in());
            }

            refreshTokenStore.upsert(userId, res.refresh_token(), refreshExpiresAt);

            log.info("[KAKAO_TOKEN] refresh_token rotated userId={}, hasExpiresAt={}",
                    userId, refreshExpiresAt != null);
        }

        return accessToken;

    }
}
