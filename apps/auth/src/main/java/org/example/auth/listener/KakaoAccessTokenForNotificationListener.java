package org.example.auth.listener;



//auth모듈에서 알림서버로 제공하는 리스너
public interface KakaoAccessTokenForNotificationListener {

    //유저아이디에 대한 엑세스 토큰을 반환한다
    String getOrRefreshAccessToken(String userId);
}
