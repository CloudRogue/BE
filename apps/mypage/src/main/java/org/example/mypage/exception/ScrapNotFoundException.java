package org.example.mypage.exception;

import lombok.Getter;
import org.example.mypage.exception.enums.ErrorCode;

@Getter
public class ScrapNotFoundException extends RuntimeException {

    private final ErrorCode errorCode = ErrorCode.SCRAP_NOT_FOUND;

    public ScrapNotFoundException() {
        super(ErrorCode.SCRAP_NOT_FOUND.message());
    }
}
