package org.example.mypage.dto.request;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import org.example.mypage.domain.enums.UiBlockType;

import java.util.List;

public record AdditionalOnboardingAnswerRequest(
        @NotEmpty List<@Valid Answer> answers
) {
    public record Answer(
            @NotNull
            @Positive
            Long additionalOnboardingId,

            @NotNull
            UiBlockType type,

            @NotNull
            Boolean unknown,

            Object value
    ) {}
}
