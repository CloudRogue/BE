package org.example.admin.api;

import org.example.admin.dto.response.AnnouncementAdminMaterialResponse;
import org.example.admin.dto.response.AnnouncementInboxResponse;

public interface AnnouncementApi {
    AnnouncementInboxResponse getNewAnnouncement();
    AnnouncementAdminMaterialResponse getAdminMaterial(Long announcementId);
}


