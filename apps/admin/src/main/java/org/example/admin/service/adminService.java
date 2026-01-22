package org.example.admin.service;

import org.example.admin.api.EligibilityCatalogResponse;
import org.example.admin.dto.request.AnnouncementDetailRequest;
import org.example.admin.dto.response.AnnouncementAdminResponse;
import org.example.admin.dto.response.AnnouncementInboxResponse;

public interface adminService {
    AnnouncementInboxResponse getNewAnnouncement();
    AnnouncementAdminResponse getAdminAnnouncement(long announcementId);
    void postAdminAnnouncement (long announcementId, AnnouncementDetailRequest request);
    EligibilityCatalogResponse getOnboardingAdminCatalog();
}
