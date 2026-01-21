package org.example.admin.service;

import lombok.RequiredArgsConstructor;
import org.example.admin.api.AIApi;
import org.example.admin.api.AnnouncementApi;
import org.example.admin.dto.response.AnnouncementAdminResponse;
import org.example.admin.dto.response.AnnouncementInboxResponse;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class adminServiceImpl implements adminService {
    private final AnnouncementApi announcementApi;
    private final AIApi aiApi;

    @Override
    public AnnouncementInboxResponse getNewAnnouncement() {
        return announcementApi.getNewAnnouncement();
    }

    @Override
    public AnnouncementAdminResponse getAdminAnnouncement(long announcementId) {
        return null;
    }


}
