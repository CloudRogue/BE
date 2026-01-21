package org.example.announcements.service;

import org.example.announcements.dto.filters.HousingTypesFilterResponse;
import org.example.announcements.dto.filters.PublishersFilterResponse;

public interface AnnouncementFilterQueryService {

    // 발행기관 목록 조회
    PublishersFilterResponse getPublishers();

    // 주택유형 목록 조회
    HousingTypesFilterResponse getHousingTypes();
}
