package org.example.mypage.profile.dto.request;


import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import org.example.mypage.profile.domain.enums.UiBlockType;
import tools.jackson.databind.JsonNode;

import java.util.List;

public record OnboardingPostRequest(
        @NotEmpty List<@Valid Answer> answers
) {
    public record Answer(
            @NotNull @Positive Long additionalOnboardingId,
            @NotNull UiBlockType type,
            @NotNull Boolean unknown,
            JsonNode value,

            List<String> options
    ) {

        @AssertTrue(message = "value type mismatch")
        public boolean isValueTypeValid() {
            if (Boolean.TRUE.equals(unknown)) {
                return value == null || value.isNull();
            }
            if (value == null || value.isNull()) return false;

            return switch (type) {
                case BOOLEAN -> value.isBoolean();
                case TEXT_INPUT, SELECT_SINGLE -> value.isTextual();
                case NUMBER_INPUT -> value.isNumber();
                case SELECT_MULTI -> value.isArray() && allText(value);
            };
        }


        @AssertTrue(message = "options required for select type")
        public boolean isOptionsRequiredForSelect() {
            return switch (type) {
                case SELECT_SINGLE, SELECT_MULTI -> options != null && !options.isEmpty();
                default -> true;
            };
        }

        @AssertTrue(message = "value must be one of options")
        public boolean isValueWithinOptions() {
            if (Boolean.TRUE.equals(unknown)) return true;

            if (type == UiBlockType.SELECT_SINGLE) {
                if (value == null || !value.isTextual()) return false;
                if (options == null || options.isEmpty()) return false;
                return options.contains(value.asText());
            }

            if (type == UiBlockType.SELECT_MULTI) {
                if (value == null || !value.isArray()) return false;
                if (options == null || options.isEmpty()) return false;

                for (JsonNode e : value) {
                    if (!e.isTextual()) return false;
                    if (!options.contains(e.asText())) return false;
                }
                return true;
            }

            return true;
        }

        @AssertTrue(message = "options not allowed for non-select type")
        public boolean isOptionsForbiddenForNonSelect() {
            return switch (type) {
                case SELECT_SINGLE, SELECT_MULTI -> true;
                default -> options == null || options.isEmpty();
            };
        }

        private static boolean allText(JsonNode arr) {
            for (JsonNode e : arr) if (!e.isTextual()) return false;
            return true;
        }
    }
}
