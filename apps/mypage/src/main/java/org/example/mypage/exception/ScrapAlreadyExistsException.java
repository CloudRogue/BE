package org.example.mypage.exception;

import lombok.Getter;
import org.example.mypage.exception.enums.ErrorCode;

@Getter
public class ScrapAlreadyExistsException extends RuntimeException {

    private final ErrorCode errorCode = ErrorCode.SCRAP_ALREADY_EXISTS;

    public ScrapAlreadyExistsException() {
        super(ErrorCode.SCRAP_ALREADY_EXISTS.message());
    }
}
