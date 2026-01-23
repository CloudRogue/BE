package org.example.announcements.dto.internal.mypage;


import java.util.List;

public record EligibilityDiagnoseRequest(
        List<RequirementItem> REQUIREMENTS_JSON,
        List<AnswerItem> ANSWERS_JSON
) {
    public record RequirementItem(
            Long additionalOnboardingId,
            String key,
            String value
    ) {}

    public record AnswerItem(
            Long additionalOnboardingId,
            String value
    ) {}
}

