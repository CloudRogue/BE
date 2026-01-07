package org.example.mypage.advice;


import org.example.mypage.exception.OnboardingIncompleteException;
import org.example.mypage.dto.response.ErrorResponse;
import org.example.mypage.exception.enums.ErrorCode;

import org.springframework.http.ResponseEntity;

import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(OnboardingIncompleteException.class)
    public ResponseEntity<ErrorResponse> handleOnboardingIncomplete(OnboardingIncompleteException e) {
        ErrorCode ec = ErrorCode.ONBOARDING_INCOMPLETE;

        return ResponseEntity.status(ec.status()).body(
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
}
