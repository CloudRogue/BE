package org.example.auth.api;

import org.example.auth.dto.ProfileCreateRequest;
import org.springframework.http.HttpStatusCode;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

@Component
public class MypageInternalClient {

    private final RestClient mypageRestClient;

    public MypageInternalClient(RestClient mypageRestClient) {
        this.mypageRestClient = mypageRestClient;
    }

    public void createProfile(String userId, String email, String nickname) {
        mypageRestClient.post()
                .uri(uriBuilder -> uriBuilder
                        .path("/internal/profile")
                        .queryParam("userId", userId)
                        .build())
                .body(new ProfileCreateRequest(email, nickname))
                .retrieve()
                .onStatus(HttpStatusCode::isError, (req, res) -> {
                    throw new RuntimeException( "mypage profile 생성 실패");
                })
                .toBodilessEntity();
    }
}
