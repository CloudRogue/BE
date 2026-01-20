package org.example.mypage.advice;


import org.example.mypage.exception.AddOnboardingException;
import org.example.mypage.exception.NotificationChannelDisabledException;
import org.example.mypage.exception.OnboardingIncompleteException;
import org.example.mypage.exception.ErrorResponse;
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

}
