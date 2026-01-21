package org.example.mypage.exception.enums;

import org.springframework.http.HttpStatus;

public enum ErrorCode {
    ONBOARDING_INCOMPLETE(HttpStatus.FORBIDDEN, "온보딩이 완료되지 않았습니다."),
    NOTIFICATION_CHANNEL_DISABLED(HttpStatus.CONFLICT, "알림 채널(이메일/카카오)이 모두 비활성화되어 리마인더를 설정할 수 없습니다."),
    ADD_ONBOARDING_DUPLICATE_KEY(HttpStatus.CONFLICT, "중복된 추가 온보딩 질문 ID가 포함되어 있습니다."),
    ADD_ONBOARDING_NOT_FOUND(HttpStatus.NOT_FOUND, "존재하지 않는 추가 온보딩 질문 ID가 포함되어 있습니다."),
    ADD_ONBOARDING_TYPE_MISMATCH(HttpStatus.BAD_REQUEST, "추가 온보딩 답변 타입이 질문 타입과 일치하지 않습니다."),
    SCRAP_ALREADY_EXISTS(HttpStatus.CONFLICT, "이미 스크랩된 공고입니다."),
    SCRAP_NOT_FOUND(HttpStatus.NOT_FOUND, "존재하지 않는 스크랩입니다.");

    private final HttpStatus httpStatus;
    private final String message;

    ErrorCode(HttpStatus httpStatus, String message) {
        this.httpStatus = httpStatus;
        this.message = message;
    }

    public String code() { return name(); }
    public int status() { return httpStatus.value(); }
    public HttpStatus httpStatus() { return httpStatus; }
    public String message() { return message; }
}
