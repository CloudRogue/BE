package org.example.announcements.dto;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class AnnouncementSummaryResponse {

    private final Long announcementId;
    private final String summary;

    public static AnnouncementSummaryResponse of(Long announcementId, String summary) {
        return new AnnouncementSummaryResponse(announcementId, summary);
    }

}
