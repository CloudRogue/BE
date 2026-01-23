package org.example.auth.dto;

public record ProfileCreateRequest(
        String email,
        String nickname
) {}
