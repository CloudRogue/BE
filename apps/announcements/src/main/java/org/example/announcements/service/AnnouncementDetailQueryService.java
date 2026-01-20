package org.example.announcements.service;

import org.example.announcements.dto.AnnouncementDetailResponse;

public interface AnnouncementDetailQueryService {

    AnnouncementDetailResponse getDetail(Long announcementId, String userId);
}
