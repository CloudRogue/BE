package org.example.announcements.service;

import lombok.RequiredArgsConstructor;
import org.example.announcements.dto.AnnouncementOpenItemResponse;
import org.example.announcements.repository.AnnouncementRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class AnnouncementSearchServiceImpl implements AnnouncementSearchService{
    private static final int MIN_LEN = 3;
    private final AnnouncementRepository announcementRepository;

    public List<AnnouncementOpenItemResponse> searchByTitlePrefix(String title) {
        String q = title == null ? "" : title.trim();
        if (q.length() < MIN_LEN) {
            return List.of();
        }

        return announcementRepository
                .findTop50ByTitleStartingWithIgnoreCaseOrderByCreatedAtDesc(q)
                .stream()
                .map(AnnouncementOpenItemResponse::from)
                .toList();
    }

}
