package org.example.announcements.controller;


import lombok.RequiredArgsConstructor;
import org.example.announcements.dto.AnnouncementDetailResponse;
import org.example.announcements.dto.AnnouncementOverviewResponse;
import org.example.announcements.dto.AnnouncementSummaryResponse;
import org.example.announcements.dto.EligibilityDiagnoseResponse;
import org.example.announcements.service.AnnouncementAiService;
import org.example.announcements.service.AnnouncementDetailQueryService;
import org.example.announcements.service.AnnouncementOverviewQueryService;
import org.example.announcements.service.AnnouncementSummaryQueryService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/announcements")
public class AnnouncementDetailController {

    private final AnnouncementDetailQueryService detailQueryService;
    private final AnnouncementSummaryQueryService summaryQueryService;
    private final AnnouncementOverviewQueryService overviewQueryService;
    private final AnnouncementAiService announcementAiService;

    @GetMapping("/{announcementId}/detail/summary")
    public ResponseEntity<AnnouncementSummaryResponse> getSummary(@PathVariable Long announcementId) {
        return ResponseEntity.ok(summaryQueryService.getSummary(announcementId));
    }

    @GetMapping("/{announcementId}/detail/overview")
    public ResponseEntity<AnnouncementOverviewResponse> getOverview(@PathVariable Long announcementId) {
        return ResponseEntity.ok(overviewQueryService.getOverview(announcementId));
    }

    //공고 상세 조회
    @GetMapping("/{announcementId}/detail")
    public ResponseEntity<AnnouncementDetailResponse> getDetail(
            @PathVariable Long announcementId,
            @AuthenticationPrincipal Jwt jwt
    ) {
        String userId = jwt.getSubject();
        return ResponseEntity.ok(detailQueryService.getDetail(announcementId, userId));
    }

    @PutMapping("/{announcementId}/detail/eligibility/check")
    public ResponseEntity<EligibilityDiagnoseResponse> eligibilityCheck(@PathVariable Long announcementId, @AuthenticationPrincipal Jwt jwt ){

        String userId = jwt.getSubject();
        return ResponseEntity.ok(announcementAiService.diagnose(announcementId, userId));
    }

}
