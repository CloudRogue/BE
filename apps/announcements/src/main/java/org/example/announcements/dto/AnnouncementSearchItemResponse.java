package org.example.announcements.dto;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.example.announcements.domain.Announcement;

import java.time.LocalDate;

@Getter
@RequiredArgsConstructor
public class AnnouncementSearchItemResponse {

    private final Long announcementId;
    private final String title;
    private final String housingType;
    private final LocalDate startDate;
    private final LocalDate endDate;
    private final LocalDate publishedAt;
    private final String publisher;
    private final String status; // OPEN / DUE_SOON / UPCOMING / CLOSED

    public static AnnouncementSearchItemResponse of(
            Announcement a,
            String status
    ) {
        return new AnnouncementSearchItemResponse(
                a.getId(),
                a.getTitle(),
                a.getHousingType(),
                a.getStartDate(),
                a.getEndDate(),
                a.getFinalPublishedAt(),
                a.getPublisher(),
                status
        );
    }
}
