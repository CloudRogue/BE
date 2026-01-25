package org.example.announcements.service;

import org.example.announcements.dto.AnnouncementOpenItemResponse;

import java.util.List;

public interface AnnouncementSearchService {
    List<AnnouncementOpenItemResponse> searchByTitlePrefix(String title);
}
