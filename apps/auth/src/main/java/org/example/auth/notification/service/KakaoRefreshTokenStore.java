package org.example.auth.notification.service;

import java.time.LocalDateTime;

public interface KakaoRefreshTokenStore {

    //리프레쉬 토큰 저장
    void upsert(String userId, String refreshTokenPlain, LocalDateTime refreshExpiresAt);

    //유저 아이돌 리프레쉬 토큰 조회(평문 반환) 오직내부에서만
    String getRequiredPlain(String userId);

    //알림허용했다가 거부할수도 있으니 두기
    void delete(String userId);
}
