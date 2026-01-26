package org.example.admin.api;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.example.admin.dto.request.AnnouncementDetailRequest;
import org.example.admin.dto.request.EligibilityBatchCreateRequest;
import org.example.admin.dto.response.AdditionalOnboardingBatchCreateResponse;
import org.example.admin.dto.response.AiQuestionsResponse;
import org.example.admin.exception.AdminPipelineFailException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientResponseException;

@Component
public class MyPageApi {
    private final RestClient restClient;
    private final ObjectMapper objectMapper;

    @Value("${mypage.api.base-url}")
    private String baseUrl;

    @Autowired
    public MyPageApi(@Qualifier("mypageRestClient") RestClient restClient, ObjectMapper objectMapper) {
        this.restClient = restClient;
        this.objectMapper = objectMapper;
    }

    public AiQuestionsResponse getAiQuestions(long announcementId) {
        String url = baseUrl + "/api/internal/ai-questions/" + announcementId;

        return restClient.get()
                .uri(url)
                .accept(MediaType.APPLICATION_JSON)
                .retrieve()
                .body(AiQuestionsResponse.class);
    }

    public EligibilityCatalogResponse getOnboardingAdminCatalog() {
        String url = baseUrl + "/api/internal/onboardings";

        return restClient.get()
                .uri(url)
                .accept(MediaType.APPLICATION_JSON)
                .retrieve()
                .body(EligibilityCatalogResponse.class);
    }


    public AdditionalOnboardingBatchCreateResponse createAdditionalOnboardings(EligibilityBatchCreateRequest request) {
        String url = baseUrl + "/api/internal/additional-onboardings";

        try {
            return restClient.post()
                    .uri(url)
                    .contentType(MediaType.APPLICATION_JSON)
                    .accept(MediaType.APPLICATION_JSON)
                    .body(request)
                    .retrieve()
                    .body(AdditionalOnboardingBatchCreateResponse.class);

        } catch (RestClientResponseException e) {
            String upstreamBody = e.getResponseBodyAsString();
            String message = extractUpstreamMessage(upstreamBody);

            throw new AdminPipelineFailException(
                    e.getStatusCode().value(),
                    "ADMIN_ADDITIONAL_ONBOARDING_CREATE_FAIL",
                    message != null ? message : "추가 온보딩 질문 생성 실패",
                    "upstream",
                    "POST " + url
            );
        }
    }


    public void postOnboarding(long announcementId, AnnouncementDetailRequest detailRequest) {
        String url = baseUrl + "/api/internal/onboardings/" + announcementId;

        OnboardingCreateRequest body = new OnboardingCreateRequest(
                new OnboardingCreateRequest.Eligibility(
                        detailRequest.eligibility().answers().stream()
                                .map(a -> new OnboardingCreateRequest.Answer(
                                        a.additionalOnboardingId(),
                                        a.type(),
                                        a.unknown(),
                                        objectMapper.valueToTree(a.value()),
                                        a.options()
                                ))
                                .toList()
                )
        );

        try {
            restClient.post()
                    .uri(url)
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(body)
                    .retrieve()
                    .toBodilessEntity();

        } catch (RestClientResponseException e) {
            String upstreamBody = e.getResponseBodyAsString();
            String message = extractUpstreamMessage(upstreamBody);

            throw new AdminPipelineFailException(
                    e.getStatusCode().value(),
                    "ADMIN_ONBOARDING_PIPELINE_FAIL",
                    message != null ? message : "어드민 지원자격 추가 실패",
                    "announcementId",
                    String.valueOf(announcementId)
            );
        }
    }

    private String extractUpstreamMessage(String body) {
        if (body == null || body.isBlank()) return null;
        try {
            JsonNode root = objectMapper.readTree(body);
            JsonNode msg = root.get("message");
            return (msg != null && msg.isTextual()) ? msg.asText() : null;
        } catch (Exception ignore) {
            return null;
        }
    }
}


