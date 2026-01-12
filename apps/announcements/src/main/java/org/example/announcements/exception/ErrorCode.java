package org.example.announcements.exception;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum ErrorCode {


    // 공고
    ANNOUNCEMENT_NOT_FOUND("ANNOUNCEMENT_NOT_FOUND", HttpStatus.NOT_FOUND, "공고를 찾을 수 없습니다."),


    // 검증/요청 오류
    VALIDATION_ERROR("VALIDATION_ERROR", HttpStatus.BAD_REQUEST, "요청 값이 올바르지 않습니다."),
    INVALID_REQUEST("INVALID_REQUEST", HttpStatus.BAD_REQUEST, "잘못된 요청입니다."),


    // 서버 애러
    INTERNAL_ERROR("INTERNAL_ERROR", HttpStatus.INTERNAL_SERVER_ERROR, "서버 오류가 발생했습니다.");

    private final String code; // (응답에 내려줄 에러 코드 문자열)
    private final HttpStatus httpStatus; // (매핑할 HTTP 상태)
    private final String defaultMessage; // (기본 메시지)
}
