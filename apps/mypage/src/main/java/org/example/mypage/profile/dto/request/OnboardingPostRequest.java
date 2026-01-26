package org.example.mypage.profile.dto.request;
import com.fasterxml.jackson.databind.JsonNode;
import jakarta.validation.Valid;
import org.example.mypage.profile.domain.enums.UiBlockType;
import java.util.List;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.validation.constraints.AssertTrue;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;



public record OnboardingPostRequest(
        @NotEmpty List<@Valid Answer> answers
) {
    public record Answer(
            @NotNull @Positive Long additionalOnboardingId,
            @NotNull UiBlockType type,
            @NotNull Boolean unknown,
            Object value,
            List<String> options
    ) {

        private static final ObjectMapper OM = new ObjectMapper();

        public JsonNode valueNode() {
            if (value == null) return null;
            if (value instanceof JsonNode j) return j;
            return OM.valueToTree(value);
        }

        @AssertTrue(message = "value type mismatch")
        public boolean isValueTypeValid() {
            JsonNode v = valueNode();

            if (Boolean.TRUE.equals(unknown)) {
                return v == null || v.isNull();
            }
            if (v == null || v.isNull()) return false;

            return switch (type) {
                case BOOLEAN -> v.isBoolean();
                case TEXT_INPUT, SELECT_SINGLE -> v.isTextual();
                case NUMBER_INPUT -> v.isNumber();
                case SELECT_MULTI -> v.isArray() && allText(v);
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

            JsonNode v = valueNode();

            if (type == UiBlockType.SELECT_SINGLE) {
                if (v == null || !v.isTextual()) return false;
                if (options == null || options.isEmpty()) return false;
                return options.contains(v.asText());
            }

            if (type == UiBlockType.SELECT_MULTI) {
                if (v == null || !v.isArray()) return false;
                if (options == null || options.isEmpty()) return false;

                for (JsonNode e : v) {
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