package org.example.auth.notification.service;

public interface KakaoAccessTokenService {

    //엑세스토큰을 반환하기(없으면 리프레쉬토큰으로 카카오에 갱신후 캐시에 저장하고 반환하기)
    String getOrRefresh(String userId);
}
