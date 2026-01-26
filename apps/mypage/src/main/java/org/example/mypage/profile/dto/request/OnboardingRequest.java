package org.example.mypage.profile.dto.request;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.validation.Valid;
import jakarta.validation.constraints.AssertTrue;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import org.example.mypage.profile.domain.enums.UiBlockType;
import java.util.List;

/**
 * 추가 온보딩 답변 제출 요청 DTO.
 *
 * <p><b>요청 구조</b></p>
 * <ul>
 *   <li>{@code answers}: 추가 온보딩 질문에 대한 답변 목록(필수, 비어있으면 안 됨)</li>
 * </ul>
 *
 * <p><b>검증 규칙</b></p>
 * <ul>
 *   <li>{@code answers}는 {@link NotEmpty}로 빈 리스트를 허용하지 않습니다.</li>
 *   <li>각 {@link Answer}는 {@link Valid}로 내부 필드 검증이 수행됩니다.</li>
 * </ul>
 */

public record OnboardingRequest(
        @NotEmpty List<@Valid Answer> answers
) {
    public record Answer(
            @NotNull @Positive Long additionalOnboardingId,
            @NotNull UiBlockType type,
            @NotNull Boolean unknown,
            Object value
    ) {
        private static final ObjectMapper OM = new ObjectMapper();

        public JsonNode valueNode() {
            if (value == null) return null;
            if (value instanceof JsonNode j) return j; // 내부 테스트/직접 생성 방어
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

        private static boolean allText(JsonNode arr) {
            for (JsonNode e : arr) if (!e.isTextual()) return false;
            return true;
        }
    }
}
