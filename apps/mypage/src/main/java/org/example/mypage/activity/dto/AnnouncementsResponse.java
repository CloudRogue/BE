package org.example.mypage.activity.dto;


import java.time.LocalDate;

public record AnnouncementsResponse(
        Long announcementId,
        String title,
        String housingType,
        LocalDate startDate,
        LocalDate endDate,
        LocalDate publishedAt,
        String publisher,
        String status // 문자열
) {
}