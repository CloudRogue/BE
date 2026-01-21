package org.example.mypage.exception;


public record ErrorResponse(
        String code,
        String message,
        int status,
        Details details
) {
    public record Details(String field, String reason) {}
}