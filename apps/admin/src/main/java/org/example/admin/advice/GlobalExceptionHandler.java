package org.example.admin.advice;

import org.example.admin.dto.response.ErrorResponse;
import org.example.admin.exception.AdminPipelineFailException;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(AdminPipelineFailException.class)
    public ResponseEntity<ErrorResponse> handleAdminPipelineFail(AdminPipelineFailException e) {
        ErrorResponse body = new ErrorResponse(
                e.getCode(),
                e.getMessage(),
                e.getStatus(),
                new ErrorResponse.Details(e.getField(), e.getReason())
        );
        return ResponseEntity.status(e.getStatus()).body(body);
    }
}
