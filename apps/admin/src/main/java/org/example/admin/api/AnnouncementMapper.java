package org.example.admin.api;

import org.example.admin.dto.response.AnnouncementAdminMaterialResponse;
import org.example.admin.dto.response.AnnouncementInboxResponse;
import org.example.announcements.domain.Announcement;

import java.time.OffsetDateTime;
import java.time.ZoneId;
import java.util.List;

public final class AnnouncementMapper {
    private static final ZoneId KST = ZoneId.of("Asia/Seoul");

    private AnnouncementMapper() {}

    public static AnnouncementInboxResponse toResponse(List<Announcement> announcements) {
        List<AnnouncementInboxResponse.Item> items = announcements.stream()
                .map(AnnouncementMapper::toItem)
                .toList();

        return new AnnouncementInboxResponse(items);
    }

    public static AnnouncementInboxResponse.Item toItem(Announcement a) {
        if (a == null) return null;

        OffsetDateTime createdAt = (a.getCreatedAt() == null)
                ? null
                : a.getCreatedAt().atZone(KST).toOffsetDateTime();

        return new AnnouncementInboxResponse.Item(
                a.getId(),
                a.getPublisher(),
                createdAt,
                a.getTitle()
        );
    }

    public static AnnouncementAdminMaterialResponse toAdminMaterial(Announcement a) {
        if (a == null) return null;

        return new AnnouncementAdminMaterialResponse(
                a.getId(),
                a.getPublisher(),
                a.getTitle(),
                a.getHousingType(),
                a.getSupplyType(),
                a.getStartDate(),
                a.getEndDate(),
                a.getDocumentPublishedAt(),
                a.getFinalPublishedAt(),
                a.getRentGtn(),
                a.getEnty(),
                a.getPrtpay(),
                a.getSurlus(),
                a.getMtRntchrg(),
                a.getFullAddress(),
                a.getRefrnLegaldongNm(),
                a.getApplyUrl(),
                a.getApplyEntryUrl()
        );
    }
}
