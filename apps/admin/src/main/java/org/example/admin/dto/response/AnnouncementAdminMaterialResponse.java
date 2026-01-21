package org.example.admin.dto.response;

import java.time.LocalDate;

public record AnnouncementAdminMaterialResponse(
        Long announcementId,
        String publisher,
        String title,
        String housingType,
        String supplyType,
        LocalDate startDate,
        LocalDate endDate,
        LocalDate documentPublishedAt,
        LocalDate finalPublishedAt,
        Long rentGtn,
        Long enty,
        Long prtpay,
        Long surlus,
        Long mtRntchrg,
        String fullAdres,
        String refrnLegaldongNm,
        String url,
        String applyUrl
) {}
