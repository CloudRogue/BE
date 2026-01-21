package org.example.announcements.listener;

import org.example.announcements.domain.Announcement;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class AnnouncementListenerImpl implements AnnouncementListener{


    @Override
    public List<Announcement> getNewAnnouncement() {
        return List.of();
    }

    public Announcement getAnnouncement(Long announcementId){return null;}
}
