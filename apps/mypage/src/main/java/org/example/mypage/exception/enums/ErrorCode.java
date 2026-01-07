package org.example.mypage.exception.enums;

import org.springframework.http.HttpStatus;

public enum ErrorCode {
    ONBOARDING_INCOMPLETE(HttpStatus.FORBIDDEN, "온보딩이 완료되지 않았습니다.");

    private final HttpStatus httpStatus;
    private final String message;

    ErrorCode(HttpStatus httpStatus, String message) {
        this.httpStatus = httpStatus;
        this.message = message;
    }

    public String code() { return name(); }
    public int status() { return httpStatus.value(); }
    public String message() { return message; }
}
