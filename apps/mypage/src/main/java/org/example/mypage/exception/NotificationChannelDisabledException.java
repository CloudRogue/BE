package org.example.mypage.exception;

import lombok.Getter;
import org.example.mypage.exception.enums.ErrorCode;

@Getter
public class NotificationChannelDisabledException extends RuntimeException {

    private final ErrorCode errorCode = ErrorCode.NOTIFICATION_CHANNEL_DISABLED;

    public NotificationChannelDisabledException() {
        super(ErrorCode.NOTIFICATION_CHANNEL_DISABLED.message());
    }

}
