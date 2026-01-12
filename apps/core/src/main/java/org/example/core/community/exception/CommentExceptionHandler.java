package org.example.core.community.exception;

import jakarta.persistence.EntityNotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class CommentExceptionHandler {

    public record ErrorResponse(
            String code,
            String message
    ){}

    // 잘못된 인자가 넘어왔을 때
    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ErrorResponse> handleIllegalArgumentException(IllegalArgumentException e) {
        ErrorResponse errorResponse = new ErrorResponse(
                "ILLEGAL_ARGUMENT",
                e.getMessage()
        );
        return ResponseEntity.status((HttpStatus.BAD_REQUEST)).body(errorResponse);
    }

    // 부모댓글이 존재하지 않을 때
    @ExceptionHandler(EntityNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleEntityNotFoundException(EntityNotFoundException e) {
        ErrorResponse errorResponse = new ErrorResponse(
                "ENTITY_NOT_FOUND",
                e.getMessage()
        );
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorResponse);
    }

    // 권한이 없는 사용자가 댓글 수정/삭제 시도할 때
    @ExceptionHandler(UnAuthorizedCommentException.class)
    public ResponseEntity<ErrorResponse> handleUnAuthorizedCommentException(UnAuthorizedCommentException e) {
        ErrorResponse errorResponse = new ErrorResponse(
                "UNAUTHORIZED_COMMENT",
                e.getMessage()
        );
        return ResponseEntity.status(HttpStatus.FORBIDDEN).body(errorResponse);
    }

    // 댓글 내용이 공백일 때
    @ExceptionHandler(BlankContentException.class)
    public ResponseEntity<ErrorResponse> handleBlankContentException(BlankContentException e) {
        ErrorResponse errorResponse = new ErrorResponse(
                "BLANK_CONTENT",
                e.getMessage()
        );
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
    }
}
