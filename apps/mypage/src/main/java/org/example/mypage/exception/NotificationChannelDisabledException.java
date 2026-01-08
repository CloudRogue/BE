package org.example.mypage.exception;

public class NotificationChannelDisabledException extends RuntimeException {
    public NotificationChannelDisabledException() {
        super("알림 채널(이메일/카카오)이 모두 비활성화되어 리마인더를 설정할 수 없습니다.");
    }
}
