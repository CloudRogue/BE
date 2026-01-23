package org.example.announcements.listener;

import lombok.RequiredArgsConstructor;
import org.example.announcements.repository.AnnouncementApplicationRepository;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class AnnouncementApplicationForNotificationListenerImpl implements AnnouncementApplicationForNotificationListener {

    private final AnnouncementApplicationRepository announcementApplicationRepository;

    @Override
    public List<String> findApplicantUserIds(Long announcementId) {
        return announcementApplicationRepository.findUserIdsByAnnouncementIdChecked(announcementId);
    }
}
