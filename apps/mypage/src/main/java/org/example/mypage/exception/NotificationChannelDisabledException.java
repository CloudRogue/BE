package org.example.mypage.exception;

import lombok.Getter;
import org.example.mypage.exception.enums.ErrorCode;


/**
 * 리마인더 설정 등 특정 기능을 수행하기 위해 필요한 알림 채널(이메일/카카오)이
 * 모두 비활성화된 경우 발생하는 비즈니스 예외입니다.
 *
 * <p><b>발생 조건</b></p>
 * <ul>
 *   <li>이메일 알림이 비활성화 AND 카카오 알림이 비활성화</li>
 * </ul>
 *
 * <p><b>API 동작</b></p>
 * <ul>
 *   <li>GlobalExceptionHandler에서 {@code ErrorCode.NOTIFICATION_CHANNEL_DISABLED}로 매핑되어 응답됩니다.</li>
 *   <li>일반적으로 HTTP 409(Conflict)로 반환합니다(정책/상태 충돌).</li>
 * </ul>
 *
 * <p><b>메시지</b></p>
 * <ul>
 *   <li>기본 메시지: {@code "알림 채널(이메일/카카오)이 모두 비활성화되어 리마인더를 설정할 수 없습니다."}</li>
 * </ul>
 *
 */
@Getter
public class NotificationChannelDisabledException extends RuntimeException {

    private final ErrorCode errorCode = ErrorCode.NOTIFICATION_CHANNEL_DISABLED;

    public NotificationChannelDisabledException() {
        super(ErrorCode.NOTIFICATION_CHANNEL_DISABLED.message());
    }

}
