package org.example.announcements.service;

import org.example.announcements.dto.AnnouncementSummaryResponse;

public interface AnnouncementSummaryQueryService {

    // 공고 요약 조회
    AnnouncementSummaryResponse getSummary(Long announcementId);
}
