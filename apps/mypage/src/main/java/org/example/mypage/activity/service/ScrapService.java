package org.example.mypage.activity.service;

import org.example.mypage.activity.dto.response.ScrapResponse;

public interface ScrapService {
    ScrapResponse getScraps(String userId, Long cursor, int limit);
    void addScrap(String userId, Long announcementId);
    void deleteScraps(String userId, Long announcementId);
}
