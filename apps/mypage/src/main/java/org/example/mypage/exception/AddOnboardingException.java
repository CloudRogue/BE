package org.example.mypage.exception;

import lombok.Getter;
import org.example.mypage.exception.enums.ErrorCode;

@Getter
public class AddOnboardingException extends RuntimeException {

    private final ErrorCode errorCode;

    public AddOnboardingException(ErrorCode errorCode) {
        super(errorCode.message());
        this.errorCode = errorCode;
    }
}
