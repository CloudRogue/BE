package org.example.announcements.dto.internal.mypage;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record MypageScrapRequest(

        @NotBlank(message = "userID는 필수입니다.")
        String userId,

        @NotNull(message = "announcementID는 필수입니다.")
        Long announcementId
) {
}
