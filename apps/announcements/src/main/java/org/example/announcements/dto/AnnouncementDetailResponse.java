package org.example.announcements.dto;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.example.announcements.domain.Announcement;

import java.time.LocalDate;

@Getter
@RequiredArgsConstructor
public class AnnouncementDetailResponse {

    private final Long announcementId;
    private final String publisher;
    private final String title;
    private final String housingType;
    private final String supplyType;
    private final LocalDate startDate;
    private final LocalDate endDate;

    private final LocalDate documentPublishedAt;
    private final LocalDate finalPublishedAt;

    private final String status;
    private final Integer dDay;

    private final Long rentGtn;
    private final Long enty;
    private final Long prtpay;
    private final Long surlus;
    private final Long mtRntchrg;

    private final String fullAddres;
    private final String refrnLegaldongNm;

    private final String url;
    private final Boolean isScrapped;

    private final String externalApplyUrl; // 신청하러가기 링크

    public static AnnouncementDetailResponse of(
            Announcement a,
            String status,
            Integer dDay,
            Boolean isScrapped,
            String externalApplyUrl
    ) {
        return new AnnouncementDetailResponse(
                a.getId(),
                a.getPublisher(),
                a.getTitle(),
                a.getHousingType(),
                a.getSupplyType(),
                a.getStartDate(),
                a.getEndDate(),
                a.getDocumentPublishedAt(),
                a.getFinalPublishedAt(),
                status,
                dDay,
                a.getRentGtn(),
                a.getEnty(),
                a.getPrtpay(),
                a.getSurlus(),
                a.getMtRntchrg(),
                a.getFullAddress(),
                a.getRefrnLegaldongNm(),
                a.getApplyUrl(),
                isScrapped,
                externalApplyUrl

        );
    }
}
