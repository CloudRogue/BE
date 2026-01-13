package org.example.announcements.service;

import org.example.announcements.api.AnnouncementSort;
import org.example.announcements.api.ApiListResponse;
import org.example.announcements.dto.AnnouncementOpenItem;

public interface AnnouncementQueryService {

    //접수중인 공고 목록 조회
    ApiListResponse<AnnouncementOpenItem> getOpen(
            AnnouncementSort sort,
            String cursor,
            int limit
    );

}
