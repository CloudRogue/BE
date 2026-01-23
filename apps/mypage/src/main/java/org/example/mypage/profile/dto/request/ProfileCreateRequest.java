package org.example.mypage.profile.dto.request;

public record ProfileCreateRequest(
        String nickname,
        String email
) {}
