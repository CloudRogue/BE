package org.example.mypage.activity.dto.response;

import java.time.LocalDate;
import java.util.List;


public record ScrapResponse(
        List<Item> items,
        Long nextCursor,
        Boolean hasNext
) {
    public record Item(
            Long announcementId,
            String title,
            String housingType,
            LocalDate startDate,
            LocalDate endDate,
            LocalDate publishedAt,
            String publisher,
            String status
    ) {
    }
}

