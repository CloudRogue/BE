package org.example.announcements.service.ingest;

import org.example.announcements.domain.Announcement;
import org.example.announcements.dto.ingest.AnnouncementIngestItem;

public final class AnnouncementIngestMapper {

    private AnnouncementIngestMapper() {}

    public static Announcement toEntity(AnnouncementIngestItem item) {
        if (item == null) return null;

        return Announcement.builder()
                .source(item.source())
                .externalKey(trimToNull(item.externalKey()))
                .publisher(trimToNull(item.publisher()))
                .title(trimToNull(item.title()))
                .housingType(trimToNull(item.housingType()))
                .supplyType(trimToNull(item.supplyType()))
                .regionName(trimToNull(item.regionName()))
                .startDate(item.startDate())
                .endDate(item.endDate())
                .documentPublishedAt(item.documentPublishedAt())
                .finalPublishedAt(item.finalPublishedAt())
                .applyUrl(trimToNull(item.applyUrl()))
                .rentGtn(item.rentGtn())
                .enty(item.enty())
                .prtpay(item.prtpay())
                .surlus(item.surlus())
                .mtRntchrg(item.mtRntchrg())
                .fullAddress(trimToNull(item.fullAddress()))
                .refrnLegaldongNm(trimToNull(item.refrnLegaldongNm()))
                .build();
    }

    private static String trimToNull(String v) {
        if (v == null) return null;
        String t = v.trim();
        return t.isEmpty() ? null : t;
    }
}
