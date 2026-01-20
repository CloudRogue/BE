package org.example.mypage.activity.api;

import org.example.mypage.activity.dto.AnnouncementsResponse;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class AnnouncementsApiImpl implements AnnouncementsApi{
    @Override
    public List<AnnouncementsResponse> getAnnouncements(List<Long> announcementIds) {
        return List.of();
    }
}
