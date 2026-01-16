package org.example.announcements.controller;


import lombok.RequiredArgsConstructor;
import org.example.announcements.dto.AnnouncementSummaryResponse;
import org.example.announcements.service.AnnouncementSummaryQueryService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/announcements")
public class AnnouncementSummaryController {

    private final AnnouncementSummaryQueryService summaryQueryService;

    @GetMapping("/{announcementId}/detail/summary")
    public ResponseEntity<AnnouncementSummaryResponse> getSummary(@PathVariable Long announcementId) {
        return ResponseEntity.ok(summaryQueryService.getSummary(announcementId));
    }
}
