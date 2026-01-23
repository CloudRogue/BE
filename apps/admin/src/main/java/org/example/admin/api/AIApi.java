package org.example.admin.api;

import org.example.admin.dto.request.AIApiRequest;
import org.example.admin.dto.response.AIApiResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientResponseException;

import java.util.List;

@Component
public class AIApi {

    private final RestClient restClient;
    private final String baseUrl;

    public AIApi(
            RestClient.Builder builder,
            @Value("${ai.api.base-url}") String baseUrl
    ) {
        this.baseUrl = baseUrl;
        this.restClient = builder.baseUrl(baseUrl).build();
    }

    public List<AIApiResponse> ingest(AIApiRequest request) {
        try {
            AIApiResponse[] arr = restClient.post()
                    .uri("/api/ingest")
                    .contentType(MediaType.APPLICATION_JSON)
                    .accept(MediaType.APPLICATION_JSON)
                    .body(request)
                    .retrieve()
                    .body(AIApiResponse[].class);

            return arr == null ? List.of() : List.of(arr);

        } catch (RestClientResponseException e) {
            throw new AiApiException(
                    "AI ingest failed. status=" + e.getStatusCode().value()
                            + ", baseUrl=" + baseUrl
                            + ", body=" + e.getResponseBodyAsString(),
                    e
            );
        }
    }

    public static class AiApiException extends RuntimeException {
        public AiApiException(String message, Throwable cause) {
            super(message, cause);
        }
    }
}
