package org.example.announcements.service;

import org.example.announcements.dto.AnnouncementOverviewResponse;

public interface AnnouncementOverviewQueryService {

    // 공고 개요 조회
    AnnouncementOverviewResponse getOverview(Long announcementId);
}
