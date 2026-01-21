package org.example.announcements.dto.internal.mypage;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import org.example.announcements.api.OutboundType;

public record MypageOutboundRequest(
        @NotBlank(message = "userID는 필수입니다.")
        String userId,

        @NotNull(message = "announcementID는 필수입니다.")
        Long announcementId,

        @NotNull(message = "type은 필수입니다")
        OutboundType type
) {
}
