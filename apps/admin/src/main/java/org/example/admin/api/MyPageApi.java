package org.example.admin.api;

import lombok.RequiredArgsConstructor;
import org.example.admin.dto.response.AiQuestionsResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

@Component
@RequiredArgsConstructor
public class MyPageApi {
    private final RestClient restClient;

    @Value("${mypage.api.base-url}")
    private String baseUrl;

    public AiQuestionsResponse getAiQuestions(long announcementId) {
        String url = baseUrl + "/internal/ai-questions/" + announcementId;

        return restClient.get()
                .uri(url)
                .accept(MediaType.APPLICATION_JSON)
                .retrieve()
                .body(AiQuestionsResponse.class);
    }
}
