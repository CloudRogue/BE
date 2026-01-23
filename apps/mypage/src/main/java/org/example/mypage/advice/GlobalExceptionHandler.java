package org.example.mypage.advice;


import org.example.mypage.exception.*;
import org.example.mypage.exception.enums.ErrorCode;

import org.springframework.http.ResponseEntity;

import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(OnboardingIncompleteException.class)
    public ResponseEntity<ErrorResponse> handleOnboardingIncomplete(OnboardingIncompleteException e) {
        ErrorCode ec = e.getErrorCode();

        return ResponseEntity.status(ec.httpStatus()).body(
                new ErrorResponse(
                        ec.code(),
                        ec.message(),
                        ec.status(),
                        new ErrorResponse.Details(
                                "onboardingCompleted",
                                "프로필이 존재하지 않아 온보딩이 완료되지 않은 상태입니다."
                        )
                )
        );
    }

    @ExceptionHandler(NotificationChannelDisabledException.class)
    public ResponseEntity<ErrorResponse> handleNotificationChannelDisabled(NotificationChannelDisabledException e) {
        ErrorCode ec = e.getErrorCode();

        return ResponseEntity.status(ec.httpStatus()).body(
                new ErrorResponse(
                        ec.code(),
                        ec.message(),
                        ec.status(),
                        new ErrorResponse.Details(
                                "channels",
                                "이메일/카카오 중 최소 1개는 활성화되어야 합니다."
                        )
                )
        );
    }

    @ExceptionHandler(AdminOnboardingException.class)
    public ResponseEntity<ErrorResponse> handleAdminOnboarding(AdminOnboardingException e) {
        ErrorCode ec = e.getErrorCode();
        return ResponseEntity.status(ec.httpStatus())
                .body(new ErrorResponse(
                        ec.code(),
                        ec.message(),
                        ec.status(),
                        new ErrorResponse.Details("eligibility", ec.message())
                ));
    }

    @ExceptionHandler(AddOnboardingException.class)
    public ResponseEntity<ErrorResponse> handleAddOnboarding(AddOnboardingException e) {
        ErrorCode ec = e.getErrorCode();

        return ResponseEntity.status(ec.httpStatus()).body(
                new ErrorResponse(
                        ec.code(),
                        ec.message(),
                        ec.status(),
                        new ErrorResponse.Details(
                                "answers",
                                ec.message()
                        )
                )
        );
    }

    @ExceptionHandler(ScrapNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleScrapNotFound(ScrapNotFoundException e) {
        ErrorCode ec = e.getErrorCode();
        return ResponseEntity.status(ec.httpStatus()).body(
                new ErrorResponse(
                        ec.code(),
                        ec.message(),
                        ec.status(),
                        new ErrorResponse.Details("announcementId", "해당 공고는 스크랩되어 있지 않습니다.")
                )
        );
    }

    @ExceptionHandler(ScrapAlreadyExistsException.class)
    public ResponseEntity<ErrorResponse> handleScrapAlreadyExists(ScrapAlreadyExistsException e) {
        ErrorCode ec = e.getErrorCode();
        return ResponseEntity.status(ec.httpStatus()).body(
                new ErrorResponse(
                        ec.code(),
                        ec.message(),
                        ec.status(),
                        new ErrorResponse.Details("announcementId", "이미 스크랩된 공고입니다.")
                )
        );
    }
}
