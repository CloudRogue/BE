package org.example.announcements.dto;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.example.announcements.domain.Announcement;

import java.time.LocalDate;

@Getter
@RequiredArgsConstructor
public class AnnouncementOpenItemResponse {

    private final Long announcementId;  // 공고 아이디
    private final String title;         // 공고명
    private final String housingType;    // 주택 유형
    private final LocalDate publishedAt; // 당첨자 발표일(= finalPublishedAt)
    private final String publisher;     // 발행처
    private final String regionName;    // 지역
    private final LocalDate startDate;  // 모집 시작일
    private final LocalDate endDate;    // 모집 마감일

    private final String status;

    public static AnnouncementOpenItemResponse from(Announcement a, String status) {
        return new AnnouncementOpenItemResponse(
                a.getId(),
                a.getTitle(),
                a.getHousingType(),
                a.getFinalPublishedAt(),
                a.getPublisher(),
                a.getRegionName(),
                a.getStartDate(),
                a.getEndDate(),
                status
        );
    }
}
