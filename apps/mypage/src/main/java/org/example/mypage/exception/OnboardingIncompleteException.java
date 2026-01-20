package org.example.mypage.exception;


import lombok.Getter;
import org.example.mypage.exception.enums.ErrorCode;

/**
 * 온보딩(프로필 생성)이 완료되지 않은 사용자가 온보딩 완료 전용 기능에 접근할 때 발생하는 비즈니스 예외입니다.
 *
 * <p><b>의미</b></p>
 * <ul>
 *   <li>인증(로그인) 여부와 별개로, 서비스 이용에 필요한 프로필/온보딩 상태가 충족되지 않았음을 나타냅니다.</li>
 * </ul>
 *
 * <p><b>API 동작</b></p>
 * <ul>
 *   <li>GlobalExceptionHandler에서 {@code ErrorCode.ONBOARDING_INCOMPLETE}로 매핑되어 응답됩니다.</li>
 *   <li>일반적으로 HTTP 403(Forbidden)로 반환합니다(권한/상태 조건 미충족).</li>
 * </ul>
 *
 * <p><b>메시지</b></p>
 * <ul>
 *   <li>기본 메시지: {@code "온보딩이 완료되지 않았습니다."}</li>
 * </ul>
 *
 */
@Getter
public class OnboardingIncompleteException extends RuntimeException {

    private final ErrorCode errorCode = ErrorCode.ONBOARDING_INCOMPLETE;

    public OnboardingIncompleteException() {
        super(ErrorCode.ONBOARDING_INCOMPLETE.message());
    }
}
