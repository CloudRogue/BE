package org.example.mypage.exception;

public class OnboardingIncompleteException extends RuntimeException {
    public OnboardingIncompleteException() {
        super("온보딩이 완료되지 않았습니다.");
    }
}
