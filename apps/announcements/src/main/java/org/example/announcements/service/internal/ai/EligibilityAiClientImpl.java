package org.example.announcements.service.internal.ai;

import lombok.RequiredArgsConstructor;
import org.example.announcements.dto.EligibilityDiagnoseResponse;
import org.example.announcements.dto.internal.mypage.EligibilityDiagnoseRequest;
import org.example.announcements.exception.BusinessException;
import org.example.announcements.exception.ErrorCode;
import org.springframework.http.HttpStatusCode;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

@Component
@RequiredArgsConstructor
public class EligibilityAiClientImpl implements EligibilityAiClient {

    private final RestClient eligibilityAiRestClient;

    @Override
    public EligibilityDiagnoseResponse diagnose(EligibilityDiagnoseRequest request) {
        return eligibilityAiRestClient.post()

                .uri("/api/eligibility/diagnose")
                .body(request)
                .retrieve()
                .onStatus(HttpStatusCode::isError, (req, res) -> {
                    throw new BusinessException(
                            ErrorCode.INTERNAL_ERROR,
                            "eligibility ai diagnose 호출 실패. status=" + res.getStatusCode().value()
                    );
                })
                .body(EligibilityDiagnoseResponse.class);
    }
}
