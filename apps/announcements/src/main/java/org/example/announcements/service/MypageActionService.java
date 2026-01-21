package org.example.announcements.service;

public interface MypageActionService {

    //기록담기
    void recordOutbound(String userId, Long announcementId);

    //스크랩추가
    void addScrap(String userId, Long announcementId);

    //스크랩제거
    void removeScrap(String userId, Long announcementId);
}
