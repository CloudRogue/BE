package org.example.announcements.service.internal.ai;


import org.example.announcements.dto.EligibilityDiagnoseResponse;
import org.example.announcements.dto.internal.mypage.EligibilityDiagnoseRequest;
import org.example.announcements.exception.BusinessException;
import org.example.announcements.exception.ErrorCode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatusCode;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

@Component
public class EligibilityAiClientImpl implements EligibilityAiClient {
    private final RestClient eligibilityAiRestClient;

    @Autowired
    public EligibilityAiClientImpl(@Qualifier("aiRestClient") RestClient eligibilityAiRestClient) {
        this.eligibilityAiRestClient = eligibilityAiRestClient;
    }

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
