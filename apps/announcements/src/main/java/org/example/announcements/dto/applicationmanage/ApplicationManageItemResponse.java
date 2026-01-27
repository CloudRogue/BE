package org.example.announcements.dto.applicationmanage;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.time.LocalDate;

@Getter
@RequiredArgsConstructor
public class ApplicationManageItemResponse {

    private final Long announcementId;
    private final String title;
    private final Long dDay;
    private final String publisher;
    private final String supplyType;

    private final String currentStatus;

    private final LocalDate endDate;
    private final LocalDate documentPublishedAt;
    private final LocalDate finalPublishedAt;
}
