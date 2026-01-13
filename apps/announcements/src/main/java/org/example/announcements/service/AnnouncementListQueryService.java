package org.example.announcements.service;

import org.example.announcements.api.AnnouncementSort;
import org.example.announcements.api.ApiListResponse;
import org.example.announcements.dto.AnnouncementOpenItemResponse;

public interface AnnouncementListQueryService {

    //접수중인 공고 목록 조회
    ApiListResponse<AnnouncementOpenItemResponse> getOpen(
            AnnouncementSort sort,
            String cursor,
            int limit
    );

}
