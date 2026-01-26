package org.example.auth.notification.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import java.time.Duration;

@Slf4j
@Component
public class KakaoAccessTokenCache {

    private static final long MIN_TTL_SECONDS = 30;

    private final RedisTemplate<String, String> redisTemplate;

    // 레디스키
    private final String keyPrefix;

    // TTL
    private final long safetyMarginSeconds;

    public KakaoAccessTokenCache(
            RedisTemplate<String, String> redisTemplate,
            @Value("${security.kakao.access-token.redis-key-prefix}") String keyPrefix,
            @Value("${security.kakao.access-token.safety-margin-seconds}") long safetyMarginSeconds
    ) {
        this.redisTemplate = redisTemplate;
        this.keyPrefix = keyPrefix;
        this.safetyMarginSeconds = safetyMarginSeconds;
    }

    //엑세스토큰 조회
    public String get(String userId) {
        return redisTemplate.opsForValue().get(key(userId));
    }

    //엑세스토큰 저장
    public void set(String userId, String accessToken, long expiresInSeconds) {


        long ttl = Math.max(expiresInSeconds - safetyMarginSeconds, MIN_TTL_SECONDS);

        redisTemplate.opsForValue().set(
                key(userId),
                accessToken,
                Duration.ofSeconds(ttl)
        );
    }

    //엑세스 토큰 삭제
    public void delete(String userId) {
        redisTemplate.delete(key(userId));
    }

    private String key(String userId) {
        return keyPrefix + userId;
    }
}
