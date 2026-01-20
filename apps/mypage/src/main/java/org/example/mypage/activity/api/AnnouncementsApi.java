package org.example.mypage.activity.api;

import org.example.mypage.activity.dto.AnnouncementsResponse;

import java.util.List;

public interface AnnouncementsApi {
    List<AnnouncementsResponse> getAnnouncements(List<Long> announcementIds);
}
