package org.example.admin.exception;

import lombok.Getter;

@Getter
public class AdminPipelineFailException extends RuntimeException {

    private final int status;
    private final String code;
    private final String field;
    private final String reason;

    public AdminPipelineFailException(int status, String code, String message, String field, String reason) {
        super(message);
        this.status = status;
        this.code = code;
        this.field = field;
        this.reason = reason;
    }
}
