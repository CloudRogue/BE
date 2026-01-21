package org.example.admin.service;

import org.example.admin.dto.response.AnnouncementAdminResponse;
import org.example.admin.dto.response.AnnouncementInboxResponse;

public interface adminService {
    AnnouncementInboxResponse getNewAnnouncement();
    AnnouncementAdminResponse getAdminAnnouncement(long announcementId);
}
