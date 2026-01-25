package org.example.notifications.api;

import lombok.RequiredArgsConstructor;
import org.example.announcements.domain.Announcement;
import org.example.announcements.listener.AnnouncementNotificationListener;
import org.example.notifications.dto.api.AnnouncementIds;
import org.example.notifications.dto.api.AnnouncementSnapshot;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.List;

@Component
@RequiredArgsConstructor
public class AnnouncementsApiImpl implements AnnouncementApi {

    private final AnnouncementNotificationListener announcementNotificationListener;

    @Override
    public AnnouncementIds findAnnouncementIdsByEndDate(LocalDate targetDate) {
        List<Long> ids = announcementNotificationListener.findByEndDate(targetDate);
        return new AnnouncementIds(ids == null ? List.of() : ids);
    }

    @Override
    public AnnouncementIds findAnnouncementIdsByDocumentPublishedAt(LocalDate targetDate) {
        List<Long> ids = announcementNotificationListener.findByDocumentPublishedAt(targetDate);
        return new AnnouncementIds(ids == null ? List.of() : ids);
    }

    @Override
    public AnnouncementSnapshot getSnapshot(Long announcementId) {
        Announcement ann = announcementNotificationListener.getCheckedAnnouncement(announcementId);

        return new AnnouncementSnapshot(
                ann.getId(),
                ann.getTitle(),
                ann.getApplyUrl()
        );
    }
}
