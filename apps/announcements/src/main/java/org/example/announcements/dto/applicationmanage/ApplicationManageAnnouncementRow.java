package org.example.announcements.dto.applicationmanage;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.time.LocalDate;

//신청관리 목록 조회시 필요한 공고필드 묶음
@Getter
@RequiredArgsConstructor
public class ApplicationManageAnnouncementRow {

    private final Long announcementId;
    private final String title;
    private final String publisher;
    private final String housingType;

    private final LocalDate endDate;
    private final LocalDate documentPublishedAt;
    private final LocalDate finalPublishedAt;
}
