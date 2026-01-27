package org.example.announcements.controller;


import lombok.RequiredArgsConstructor;
import org.example.announcements.dto.AnnouncementDetailResponse;
import org.example.announcements.dto.AnnouncementOverviewResponse;
import org.example.announcements.dto.AnnouncementSummaryResponse;
import org.example.announcements.dto.EligibilityDiagnoseResponse;
import org.example.announcements.exception.BusinessException;
import org.example.announcements.service.AnnouncementAiService;
import org.example.announcements.service.AnnouncementDetailQueryService;
import org.example.announcements.service.AnnouncementOverviewQueryService;
import org.example.announcements.service.AnnouncementSummaryQueryService;
import org.example.auth.dto.UsersPrincipal;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import static org.example.announcements.exception.ErrorCode.UNAUTHORIZED;

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
            @AuthenticationPrincipal UsersPrincipal principal
    ) {
        String userId = (principal != null) ? principal.getName() : null; // 비회원이면 null
        return ResponseEntity.ok(detailQueryService.getDetail(announcementId, userId));
    }


    @PutMapping("/{announcementId}/detail/eligibility/check")
    public ResponseEntity<EligibilityDiagnoseResponse> eligibilityCheck(@PathVariable Long announcementId, @AuthenticationPrincipal UsersPrincipal principal ){
        if (principal == null) throw new BusinessException(UNAUTHORIZED, "비로그인/토큰 만료");
        String userId = principal.getName();
        return ResponseEntity.ok(announcementAiService.diagnose(announcementId, userId));
    }

}
