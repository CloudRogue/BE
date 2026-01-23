package org.example.announcements.exception;

import jakarta.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.stereotype.Component;

import java.util.LinkedHashMap;
import java.util.Map;

@RestControllerAdvice(basePackages = "org.example.announcements")
@Component("announcementsExceptionHanlder")
public class GlobalExceptionHandler {

    private static final Logger log = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    // 비즈니스 예외
    @ExceptionHandler(BusinessException.class)
    public ResponseEntity<ErrorResponse> handleBusiness(BusinessException e) { // 비즈니스 예외 처리
        ErrorCode ec = e.getErrorCode();

        ErrorResponse body = ErrorResponse.builder() // ErrorResponse 생성
                .code(ec.getCode())
                .message(e.getMessage())
                .status(ec.getHttpStatus().value())
                .details(e.getDetails())
                .build();

        return ResponseEntity.status(ec.getHttpStatus()).body(body);
    }

    // @Valid 바디 검증 실패
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleValidation(MethodArgumentNotValidException e) {
        Map<String, String> fieldErrors = new LinkedHashMap<>();
        for (FieldError fe : e.getBindingResult().getFieldErrors()) {
            fieldErrors.put(fe.getField(), fe.getDefaultMessage());
        }

        Map<String, Object> details = new LinkedHashMap<>();
        details.put("fields", fieldErrors);

        ErrorCode ec = ErrorCode.VALIDATION_ERROR;

        ErrorResponse body = ErrorResponse.builder()
                .code(ec.getCode())
                .message(ec.getDefaultMessage())
                .status(ec.getHttpStatus().value())
                .details(details)
                .build();

        return ResponseEntity.status(ec.getHttpStatus()).body(body);
    }

    // 잘못된 요청
    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ErrorResponse> handleIllegalArgument(IllegalArgumentException e) {
        ErrorCode ec = ErrorCode.INVALID_REQUEST;

        ErrorResponse body = ErrorResponse.builder()
                .code(ec.getCode())
                .message(e.getMessage() == null ? ec.getDefaultMessage() : e.getMessage())
                .status(ec.getHttpStatus().value())
                .details(null)
                .build();

        return ResponseEntity.status(ec.getHttpStatus()).body(body);
    }

    // 나머지 예외
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleAny(Exception e, HttpServletRequest request) {
        // 서버 에러는 위험한 오류라고 생각되어 메시지로 원인을 밝혀내기
        log.error("Unhandled exception. path={}, message={}", request.getRequestURI(), e.getMessage(), e);

        ErrorCode ec = ErrorCode.INTERNAL_ERROR;

        ErrorResponse body = ErrorResponse.builder()
                .code(ec.getCode())
                .message(ec.getDefaultMessage())
                .status(ec.getHttpStatus().value())
                .details(null)
                .build();

        return ResponseEntity.status(ec.getHttpStatus()).body(body);
    }
}
