package org.example.auth.dto;

public record TokenResponseDto(
        String accessToken,
        String refreshToken
) { }
