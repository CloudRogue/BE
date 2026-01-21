package org.example.announcements.service.ingest;

import org.example.announcements.domain.Announcement;
import org.example.announcements.dto.ingest.AnnouncementIngestItem;

public final class AnnouncementIngestMapper {

    private AnnouncementIngestMapper() {}

    public static Announcement toEntity(AnnouncementIngestItem item,String regionCode, String applyEntryUrl) {
        if (item == null) return null;

        return Announcement.create(
                item.source(),
                trimToNull(item.externalKey()),
                trimToNull(item.title()),
                trimToNull(item.publisher()),
                trimToNull(item.housingType()),
                trimToNull(item.supplyType()),
                trimToNull(regionCode),
                trimToNull(item.regionName()),
                item.startDate(),
                item.endDate(),
                item.documentPublishedAt(),
                item.finalPublishedAt(),
                trimToNull(item.applyUrl()),
                trimToNull(applyEntryUrl),
                item.rentGtn(),
                item.enty(),
                item.prtpay(),
                item.surlus(),
                item.mtRntchrg(),
                trimToNull(item.fullAddress()),
                trimToNull(item.refrnLegaldongNm())
        );
    }

    private static String trimToNull(String v) {
        if (v == null) return null;
        String t = v.trim();
        return t.isEmpty() ? null : t;
    }
}
