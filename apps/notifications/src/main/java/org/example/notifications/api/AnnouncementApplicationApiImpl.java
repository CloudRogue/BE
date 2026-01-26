package org.example.notifications.api;

import lombok.RequiredArgsConstructor;
import org.example.announcements.listener.AnnouncementApplicationForNotificationListener;
import org.example.notifications.dto.api.AppliedUserIds;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class AnnouncementApplicationApiImpl implements AnnouncementApplicationApi {

    private final AnnouncementApplicationForNotificationListener announcementApplicationForNotificationListener;


    @Override
    public AppliedUserIds findApplicationUserIds(Long announcementId) {
        List<String> userIds = announcementApplicationForNotificationListener.findApplicantUserIds(announcementId);

        return new AppliedUserIds(userIds == null ? List.of() : userIds);
    }
}
