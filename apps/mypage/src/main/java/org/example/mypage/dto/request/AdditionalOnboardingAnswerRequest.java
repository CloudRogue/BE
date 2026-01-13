package org.example.mypage.dto.request;

import com.fasterxml.jackson.databind.JsonNode;
import jakarta.validation.Valid;
import jakarta.validation.constraints.AssertTrue;
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

            JsonNode value
    ) {

        @AssertTrue(message = "value type mismatch")
        public boolean isValueTypeValid() {
            if (Boolean.TRUE.equals(unknown)) return true;
            if (value == null || value.isNull()) return false;

            return switch (type) {
                case BOOLEAN -> value.isBoolean();

                case TEXT_INPUT, SELECT_SINGLE -> value.isTextual();

                case NUMBER_INPUT -> value.isNumber();

                case SELECT_MULTI -> value.isArray() && allText(value); // 배열 + 원소가 전부 문자열
            };
        }
        private static boolean allText(JsonNode arr) {
            for (JsonNode e : arr) {
                if (!e.isTextual()) return false;
            }
            return true;
        }
    }
}
