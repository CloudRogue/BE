package org.example.mypage.profile.dto.response;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import org.example.mypage.profile.dto.OnboardingAnswerVO;

import java.util.List;

/**
 * 필수 온보딩(프로필) 답변 조회 응답 DTO.
 *
 * <p>서버가 요구하는 필수 온보딩 질문 목록과, 사용자가 이미 입력한 값(value)을 함께 제공합니다.</p>
 */
public record OnboardingProfileResponse(

        @NotEmpty
        List<@NotNull OnboardingAnswerVO> requiredOnboardingAnswers,

        @NotEmpty
        List<@NotNull OnboardingAnswerVO> additionalOnboardingAnswers

) {

}
