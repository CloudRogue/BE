package org.example.announcements.service;

import lombok.RequiredArgsConstructor;
import org.example.announcements.dto.AnnouncementOpenItemResponse;
import org.example.announcements.repository.AnnouncementRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

import static org.example.announcements.util.AnnouncementStatusUtil.calcStatus;

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

        LocalDate today = LocalDate.now();

        return announcementRepository
                .findTop50ByTitleStartingWithIgnoreCaseOrderByCreatedAtDesc(q)
                .stream()
                .map(a -> AnnouncementOpenItemResponse.from(
                        a,
                        calcStatus(a.getStartDate(), a.getEndDate(), today)
                ))
                .toList();
    }

}
