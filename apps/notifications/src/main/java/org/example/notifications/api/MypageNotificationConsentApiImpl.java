package org.example.notifications.api;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class MypageNotificationConsentApiImpl implements MypageNotificationConsentApi {

    private final RestClient mypageRestClient;

    @Override
    public List<String> findKakaoAllowedUserIds() {
        try {
            List<?> res = mypageRestClient.get()
                    .uri("/internal/mypage/notification-consents/kakao/allowed-users")
                    .retrieve()
                    .body(List.class);

            if (res == null) return List.of();

            return res.stream().map(String::valueOf).toList();

        } catch (Exception e) {
            log.error("[notif] failed to fetch kakao allowed users from mypage", e);
            return List.of();
        }
    }
}
