package org.example.mypage.dto.request;

import jakarta.validation.constraints.NotNull;

public record KakaoNotificationSettingPatchRequest(
        @NotNull Boolean enabled
) {}
