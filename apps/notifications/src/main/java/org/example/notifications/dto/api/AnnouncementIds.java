package org.example.notifications.dto.api;

import java.util.List;

//공고아이디 목록받는 디티오
public record AnnouncementIds(
        List<Long> announcementIds
) {}