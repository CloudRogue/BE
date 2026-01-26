package org.example.admin.api;



import tools.jackson.databind.JsonNode;

import java.util.List;

public record OnboardingCreateRequest(
        Eligibility eligibility
) {
    public record Eligibility(
            List<Answer> answers
    ) {}

    public record Answer(
            long additionalOnboardingId,
            String type,
            boolean unknown,
            JsonNode value,
            List<String> options
    ) {}
}
