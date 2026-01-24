package org.example.notifications.api;

import org.example.notifications.dto.api.AnnouncementIds;
import org.example.notifications.dto.api.AnnouncementSnapshot;

import java.time.LocalDate;
import java.util.List;

// 알림 -> 공고 모듈로
public interface AnnouncementApi {

    //접수마감일이 targetDate인 공고들의 목록 조회
    AnnouncementIds findAnnouncementIdsByEndDate(LocalDate targetDate);

    //서류발표일이 targetDate인 공고들의 목록 조회
    AnnouncementIds findAnnouncementIdsByDocumentPublishedAt(LocalDate targetDate);

    //알림 문구나 버튼 생성을 위한 공고정보 가져오기
    AnnouncementSnapshot getSnapshot(Long announcementId);

}
