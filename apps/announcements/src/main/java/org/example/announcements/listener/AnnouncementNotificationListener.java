package org.example.announcements.listener;

import org.example.announcements.domain.Announcement;

import java.time.LocalDate;
import java.util.List;

public interface AnnouncementNotificationListener {

    //특정날짜가 접수 마감일인 공고 목록 조회
    List<Long> findByEndDate(LocalDate targetDate);

    //특정날짜가 서류발표일인 공고목록 조회
    List<Long> findByDocumentPublishedAt(LocalDate targetDate);

    //공고 단건조회
    Announcement getCheckedAnnouncement(Long announcementId);
}
