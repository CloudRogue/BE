package org.example.announcements.dto.internal.mypage;

import java.util.List;

public record MypagePersonalizedResponse(
        List<Long> announcementIds
) {}
