package org.example.admin.api;

import com.fasterxml.jackson.databind.JsonNode;

import java.util.List;

public record EligibilityAnswersRequest(OnboardingRequest eligibility) {
    public record OnboardingRequest(List<Answer> answers) {
        public record Answer(
                Long additionalOnboardingId,
                UiBlockType type,
                Boolean unknown,
                JsonNode value
        ) {}
    }

    public enum UiBlockType {
        BOOLEAN,
        SELECT_SINGLE,
        SELECT_MULTI,
        TEXT_INPUT,
        NUMBER_INPUT
    }


}

