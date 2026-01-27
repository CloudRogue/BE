package org.example.announcements.controller;

import lombok.RequiredArgsConstructor;
import org.example.announcements.dto.ApplicationManagePrepareDetailResponse;
import org.example.announcements.exception.BusinessException;
import org.example.announcements.service.ApplicationManagePrepareDetailQueryService;
import org.example.auth.dto.UsersPrincipal;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import static org.example.announcements.exception.ErrorCode.UNAUTHORIZED;


@RestController
@RequiredArgsConstructor
@RequestMapping("/api/announcements")
public class ApplicationManagePrepareDetailController {

    private final ApplicationManagePrepareDetailQueryService queryService;

    // 신청관리 - 준비화면 데이터조회 (회원 전용)
    @GetMapping("/application-manage/{announcementId}/detail")
    public ResponseEntity<ApplicationManagePrepareDetailResponse> getPrepareDetail(
            @PathVariable Long announcementId,
            @AuthenticationPrincipal UsersPrincipal principal
    ) {
        if (principal == null) throw new BusinessException(UNAUTHORIZED, "비로그인/토큰 만료");
        String userId = principal.getName();
        return ResponseEntity.ok(queryService.getDetail(userId, announcementId));
    }
}
