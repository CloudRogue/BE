package org.example.announcements.controller;


import lombok.RequiredArgsConstructor;
import org.example.announcements.dto.AnnouncementOverviewResponse;
import org.example.announcements.dto.AnnouncementSummaryResponse;
import org.example.announcements.service.AnnouncementOverviewQueryService;
import org.example.announcements.service.AnnouncementSummaryQueryService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/announcements")
public class AnnouncementDetailController {

    private final AnnouncementSummaryQueryService summaryQueryService;
    private final AnnouncementOverviewQueryService overviewQueryService;

    @GetMapping("/{announcementId}/detail/summary")
    public ResponseEntity<AnnouncementSummaryResponse> getSummary(@PathVariable Long announcementId) {
        return ResponseEntity.ok(summaryQueryService.getSummary(announcementId));
    }

    @GetMapping("/{announcementId}/detail/overview")
    public ResponseEntity<AnnouncementOverviewResponse> getOverview(@PathVariable Long announcementId) {
        return ResponseEntity.ok(overviewQueryService.getOverview(announcementId));
    }
}
