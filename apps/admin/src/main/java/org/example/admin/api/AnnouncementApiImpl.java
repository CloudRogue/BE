package org.example.admin.api;

import lombok.RequiredArgsConstructor;
import org.example.admin.dto.response.AnnouncementAdminMaterialResponse;
import org.example.admin.dto.response.AnnouncementInboxResponse;
import org.example.announcements.domain.Announcement;
import org.example.announcements.listener.AnnouncementListener;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class AnnouncementApiImpl implements AnnouncementApi{
    private final AnnouncementListener announcementListener;


    @Override
    public AnnouncementInboxResponse getNewAnnouncement() {
        List<Announcement> announcements = announcementListener.getNewAnnouncement();
        return AnnouncementMapper.toResponse(announcements);
    }


    @Override
    public AnnouncementAdminMaterialResponse getAdminMaterial(Long announcementId) {
        Announcement a = announcementListener.getAnnouncement(announcementId);
        return AnnouncementMapper.toAdminMaterial(a);
    }

}
