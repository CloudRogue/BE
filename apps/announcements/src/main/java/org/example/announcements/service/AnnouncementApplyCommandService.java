package org.example.announcements.service;

public interface AnnouncementApplyCommandService {

    //내부 지원관리 기록
    void apply(String userId, Long announcementId);
}
