package org.example.auth.notification.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.auth.notification.config.TokenCipher;
import org.example.auth.notification.domain.KakaoOAuthToken;
import org.example.auth.notification.repository.KakaoOAuthTokenRepository;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class JpaKakaoRefreshTokenStore implements KakaoRefreshTokenStore {

    private final KakaoOAuthTokenRepository tokenRepository;
    private final TokenCipher tokenCipher;


    //유저아이디 기준 upsert
    @Transactional
    @Override
    public void upsert(String userId, String refreshTokenPlain, LocalDateTime refreshExpiresAt) {
        // 평문 -> 암호문
        String refreshTokenEnc = tokenCipher.encrypt(refreshTokenPlain);

        // upsert
        tokenRepository.findById(userId).ifPresentOrElse(existing -> {

            existing.update(refreshTokenEnc, refreshExpiresAt);
            tokenRepository.save(existing);
            log.info("[KAKAO_RT] updated userId={}, hasExpiresAt={}",
                    userId, refreshExpiresAt != null);

        }, () -> {

            KakaoOAuthToken created = KakaoOAuthToken.create(userId, refreshTokenEnc, refreshExpiresAt);

            try {
                tokenRepository.save(created);
                log.info("[KAKAO_RT] created userId={}, hasExpiresAt={}",
                        userId, refreshExpiresAt != null);

            } catch (DataIntegrityViolationException dup) {
                // 동시성 방어
                KakaoOAuthToken existing = tokenRepository.findById(userId)
                        .orElseThrow(() -> new IllegalStateException("KakaoOAuthToken vanished after duplicate insert"));
                existing.update(refreshTokenEnc, refreshExpiresAt);
                tokenRepository.save(existing);

                log.info("[KAKAO_RT] race-upsert resolved userId={}, hasExpiresAt={}",
                        userId, refreshExpiresAt != null);
            }
        });

    }

    //카카오 엑세스 갱신에서만 사용
    @Override
    public String getRequiredPlain(String userId) {
        KakaoOAuthToken token = tokenRepository.findById(userId)
                .orElseThrow(() -> new IllegalStateException("NO_REFRESH_TOKEN userId=" + userId));

        return tokenCipher.decrypt(token.getRefreshTokenEnc());
    }

    //도중에 알림허용 거부했을때를 위해서
    @Transactional
    @Override
    public void delete(String userId) {
        tokenRepository.deleteById(userId);
        log.info("[KAKAO_RT] deleted userId={}", userId);
    }
}
