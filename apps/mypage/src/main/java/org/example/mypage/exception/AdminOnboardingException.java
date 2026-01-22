package org.example.mypage.exception;


import lombok.Getter;
import org.example.mypage.exception.enums.ErrorCode;

@Getter
public class AdminOnboardingException extends RuntimeException {

    private final ErrorCode errorCode;

    public AdminOnboardingException(ErrorCode errorCode) {
        super(errorCode.message());
        this.errorCode = errorCode;
    }
}
