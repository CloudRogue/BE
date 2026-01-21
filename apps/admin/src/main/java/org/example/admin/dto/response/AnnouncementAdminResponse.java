package org.example.admin.dto.response;

import java.time.LocalDate;
import java.util.List;

public record AnnouncementAdminResponse(
        long announcementId,
        String publisher,
        String title,
        String housingType,
        String supplyType,
        LocalDate startDate,
        LocalDate endDate,
        LocalDate documentPublishedAt,
        LocalDate finalPublishedAt,
        String status,
        int dDay,
        long rentGtn,
        long enty,
        long prtpay,
        long surlus,
        long mtRntchrg,
        String fullAdres,
        String refrnLegaldongNm,
        String url,
        String applyUrl,
        boolean isScrapped,
        List<KvDigestItem> kvDigest
) {
    public record KvDigestItem(
            String key,
            String value
    ) {}
}
