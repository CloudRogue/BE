package org.example.mypage.activity.service;

import org.example.mypage.activity.domain.Scrap;


import java.util.List;

public interface ScrapService {
    List<Scrap> fetchForScroll(String userId, Long cursor, int sizePlusOne);
    void addScrap(String userId, Long announcementId);
    void deleteScraps(String userId, Long announcementId);
}
