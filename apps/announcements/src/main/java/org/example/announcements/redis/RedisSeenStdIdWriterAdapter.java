package org.example.announcements.redis;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

import java.util.Collection;
import java.util.Locale;

@Component
@RequiredArgsConstructor
public class RedisSeenStdIdWriterAdapter implements SeenStdIdWriterPort {

    // Redis key 조립 상수화
    private static final String PREFIX = "seoulhousing";
    private static final String SEEN = "seen";
    private static final String SEP = ":";

    private final StringRedisTemplate stringRedisTemplate;

    @Value("${announcements.ingest.env}")
    private String ingestEnv;

    @Override
    public void addSeenStdIds(String source, String category, String scope, Collection<String> stdIds) {
        if (stdIds == null || stdIds.isEmpty()) return;

        // 파싱서버와 동일하게 키생성
        String key = seenKey(source, category, scope);

        String[] members = stdIds.stream()
                .filter(s -> s != null && !s.isBlank())
                .map(String::trim)
                .toArray(String[]::new);

        if (members.length == 0) return;

        stringRedisTemplate.opsForSet().add(key, members);
    }

    //seen stdId전용 키 샹성하는 메서드
    private String seenKey(String source, String category, String scope) {
        return PREFIX + SEP + norm(ingestEnv)
                + SEP + SEEN + SEP + norm(source)
                + SEP + norm(category)
                + SEP + norm(scope);
    }

    //정규화
    private static String norm(String raw) {
        if (raw == null || raw.isBlank()) {
            throw new IllegalArgumentException("seen key param is blank");
        }
        return raw.trim().toLowerCase(Locale.ROOT);
    }
}
