package org.example.announcements.exception;

import lombok.Getter;

@Getter
public class BusinessException extends RuntimeException {

    private final ErrorCode errorCode; // 에러코드
    private final Object details; // 추가정보

    public BusinessException(ErrorCode errorCode) {
        super(errorCode.getDefaultMessage());
        this.errorCode = errorCode;
        this.details = null;
    }

    public BusinessException(ErrorCode errorCode, String message) { // 메시지 재정의 생성자
        super(message);
        this.errorCode = errorCode;
        this.details = null;
    }

    public BusinessException(ErrorCode errorCode, String message, Object details) { // 메시지+details 생성자
        super(message);
        this.errorCode = errorCode;
        this.details = details;
    }

    public BusinessException(ErrorCode errorCode, Object details) { // details만 추가하는 생성자
        super(errorCode.getDefaultMessage());
        this.errorCode = errorCode;
        this.details = details;
    }

}
