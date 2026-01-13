package org.example.announcements.dto;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.example.announcements.domain.Announcement;

import java.time.LocalDate;

@Getter
@RequiredArgsConstructor
public class AnnouncementOpenItem {

    private final Long announcementId;  // 공고 아이디
    private final String title;         // 공고명
    private final String publisher;     // 발행처
    private final String regionName;    // 지역
    private final LocalDate startDate;  // 모집 시작일
    private final LocalDate endDate;    // 모집 마감일

    public static AnnouncementOpenItem from(Announcement a) {
        return new AnnouncementOpenItem(
                a.getId(),
                a.getTitle(),
                a.getPublisher(),
                a.getRegionName(),
                a.getStartDate(),
                a.getEndDate()
        );
    }
}
