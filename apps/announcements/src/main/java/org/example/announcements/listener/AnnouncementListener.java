package org.example.announcements.listener;

import org.example.announcements.domain.Announcement;

import java.util.List;

public interface AnnouncementListener {
    List<Announcement> getNewAnnouncement();
    Announcement getAnnouncement(Long announcementId);


}
