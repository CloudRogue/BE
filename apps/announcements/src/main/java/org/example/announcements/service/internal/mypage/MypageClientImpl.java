package org.example.announcements.service.internal.mypage;

import lombok.RequiredArgsConstructor;
import org.example.announcements.dto.internal.mypage.MypageOutboundRequest;
import org.example.announcements.dto.internal.mypage.MypageScrapRequest;
import org.example.announcements.exception.BusinessException;
import org.example.announcements.exception.ErrorCode;
import org.springframework.http.HttpStatusCode;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

import static org.springframework.http.HttpMethod.*;

@Component
@RequiredArgsConstructor
public class MypageClientImpl  implements MypageClient {

    private final RestClient mypageRestClient;

    @Override
    public void postOutbound(MypageOutboundRequest request) {
        mypageRestClient.post()
                .uri("/internal/mypage/outbound")
                .body(request)
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
                .uri("/internal/mypage/scraps")
                .body(request)
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

        mypageRestClient.method(DELETE)
                .uri("/internal/mypage/scraps")
                .body(request)
                .retrieve()
                .onStatus(HttpStatusCode::isError, (req, res) -> {
                    throw new BusinessException(
                            ErrorCode.INTERNAL_ERROR,
                            "mypage scrap 해제 호출 실패. status=" + res.getStatusCode().value()
                    );
                })
                .toBodilessEntity();
    }
}
