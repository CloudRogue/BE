package org.example.admin.dto.response;

import java.time.OffsetDateTime;
import java.util.List;

public record AnnouncementInboxResponse (
        List<Item> data
) {
    public record Item(
            Long announcementId,
            String publisher,
            OffsetDateTime createdAt,
            String title
    ) {}
}
