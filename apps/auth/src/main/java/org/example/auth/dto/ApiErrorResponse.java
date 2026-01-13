package org.example.auth.dto;

public record ApiErrorResponse(
        String code,
        String message

) {
    public static ApiErrorResponse of(String code, String message) {
        return new ApiErrorResponse(code, message);
    }
}