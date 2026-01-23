package org.example.notifications.dto.api;

//알림모듈에서만 사용하는 공고 스냅샷
public record AnnouncementSnapshot(
        Long announcementId,
        String title,
        String applyUrl // 원본 공고(LH/SH) 링크
) {}
