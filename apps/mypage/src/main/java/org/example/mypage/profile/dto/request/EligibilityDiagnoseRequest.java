package org.example.mypage.profile.dto.request;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import java.util.List;


public record EligibilityDiagnoseRequest(
        @NotEmpty @Valid List<RequirementItem> REQUIREMENTS_JSON,
        @NotEmpty @Valid List<AnswerItem> ANSWERS_JSON
) {

    public record RequirementItem(
            @NotNull @PositiveOrZero Long additionalOnboardingId,
            @NotNull String key,
            @NotNull String value
    ) {}

    public record AnswerItem(
            @NotNull @PositiveOrZero Long additionalOnboardingId,
            String value
    ) {}
}

