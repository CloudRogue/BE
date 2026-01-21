package org.example.announcements.service;

public interface AnnouncementApplyCommandService {

    //지원관리 담기
    void apply(String userId, Long announcementId);
}
