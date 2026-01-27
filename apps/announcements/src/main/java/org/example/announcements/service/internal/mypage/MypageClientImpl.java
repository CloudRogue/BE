package org.example.announcements.service.internal.mypage;


import org.example.announcements.dto.internal.mypage.EligibilityDiagnoseRequest;
import org.example.announcements.dto.internal.mypage.MypageOutboundRequest;
import org.example.announcements.dto.internal.mypage.MypagePersonalizedResponse;
import org.example.announcements.dto.internal.mypage.MypageScrapRequest;
import org.example.announcements.exception.BusinessException;
import org.example.announcements.exception.ErrorCode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatusCode;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

import java.util.List;

import static org.springframework.http.HttpMethod.*;

@Component
public class MypageClientImpl  implements MypageClient {

    private final RestClient mypageRestClient;

    @Autowired
    public MypageClientImpl(@Qualifier("mypageRestClient") RestClient mypageRestClient) {
        this.mypageRestClient = mypageRestClient;
    }

    @Override
    public void postOutbound(MypageOutboundRequest request) {
        mypageRestClient.post()
                .uri(uriBuilder -> uriBuilder
                        .path("/api/internal/mypage/outbound")
                        .queryParam("userId", request.userId())
                        .queryParam("announcementId", request.announcementId())
                        .build()
                )
                .retrieve()
                .onStatus(HttpStatusCode::isError, (req, res) -> {
                    throw new BusinessException(
                            ErrorCode.INTERNAL_ERROR,
                            "mypage outbound 호출 실패. status=" + res.getStatusCode().value()
                    );
                })
                .toBodilessEntity();
    }

    @Override
    public void postScrap(MypageScrapRequest request) {
        mypageRestClient.post()
                .uri(uriBuilder -> uriBuilder
                        .path("/api/internal/mypage/scraps")
                        .queryParam("userId", request.userId())
                        .queryParam("announcementId", request.announcementId())
                        .build()
                )
                .retrieve()
                .onStatus(HttpStatusCode::isError, (req, res) -> {
                    throw new BusinessException(
                            ErrorCode.INTERNAL_ERROR,
                            "mypage scrap 추가 호출 실패. status=" + res.getStatusCode().value()
                    );
                })
                .toBodilessEntity();
    }

    @Override
    public void deleteScrap(MypageScrapRequest request) {
        mypageRestClient.delete()
                .uri(uriBuilder -> uriBuilder
                        .path("/api/internal/mypage/scraps")
                        .queryParam("userId", request.userId())
                        .queryParam("announcementId", request.announcementId())
                        .build()
                )
                .retrieve()
                .onStatus(HttpStatusCode::isError, (req, res) -> {
                    throw new BusinessException(
                            ErrorCode.INTERNAL_ERROR,
                            "mypage scrap 해제 호출 실패. status=" + res.getStatusCode().value()
                    );
                })
                .toBodilessEntity();
    }

    @Override
    public MypagePersonalizedResponse getPersonalized(String userId) {

        List<Long> ids = mypageRestClient.get()
                .uri(uriBuilder -> uriBuilder
                        .path("/api/internal/personalized")
                        .queryParam("userId", userId)
                        .build()
                )
                .retrieve()
                .onStatus(status -> status.value() == 401, (req, res) -> {
                    throw new BusinessException(ErrorCode.UNAUTHORIZED, "비로그인/토큰 만료");
                })
                .onStatus(status -> status.value() == 403, (req, res) -> {
                    throw new BusinessException(ErrorCode.FORBIDDEN, "온보딩 미완료");
                })
                .onStatus(HttpStatusCode::isError, (req, res) -> {
                    throw new BusinessException(
                            ErrorCode.INTERNAL_ERROR,
                            "mypage personalized 호출 실패. status=" + res.getStatusCode().value()
                    );
                })
                .body(new org.springframework.core.ParameterizedTypeReference<>() {
                });

        if (ids == null) ids = java.util.Collections.emptyList();
        return new MypagePersonalizedResponse(ids);
    }

    @Override
    public EligibilityDiagnoseRequest getDiagnose(long announcementId, String userId) {
        return mypageRestClient.get()
                .uri(uriBuilder -> uriBuilder
                        .path("/api/internal/diagnose/{announcementId}")
                        .queryParam("userId", userId)
                        .build(announcementId)
                )
                .retrieve()
                .onStatus(status -> status.value() == 401, (req, res) -> {
                    throw new BusinessException(ErrorCode.UNAUTHORIZED, "비로그인/토큰 만료");
                })
                .onStatus(status -> status.value() == 403, (req, res) -> {
                    throw new BusinessException(ErrorCode.FORBIDDEN, "온보딩 미완료");
                })
                .onStatus(HttpStatusCode::isError, (req, res) -> {
                    throw new BusinessException(
                            ErrorCode.INTERNAL_ERROR,
                            "mypage diagnose 요청 생성 호출 실패. status=" + res.getStatusCode().value()
                    );
                })
                .body(EligibilityDiagnoseRequest.class);
    }


}
