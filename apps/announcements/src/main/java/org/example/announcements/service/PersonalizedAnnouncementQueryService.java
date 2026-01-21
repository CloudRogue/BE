package org.example.announcements.service;

import org.example.announcements.api.ApiListResponse;
import org.example.announcements.api.PersonalizedSort;
import org.example.announcements.dto.AnnouncementSearchItemResponse;

public interface PersonalizedAnnouncementQueryService {

    // 맞춤공고 목록 조회
    ApiListResponse<AnnouncementSearchItemResponse> getPersonalized(
            String userId,
            String cursor,
            int limit,
            PersonalizedSort sort
    );
}
