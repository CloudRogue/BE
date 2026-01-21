package org.example.announcements.controller;

import lombok.RequiredArgsConstructor;
import org.example.announcements.dto.ApplicationManagePrepareDetailResponse;
import org.example.announcements.service.ApplicationManagePrepareDetailQueryService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/announcements")
public class ApplicationManagePrepareDetailController {

    private final ApplicationManagePrepareDetailQueryService queryService;

    //신청관리 - 준비화면 데이터조회
    @GetMapping("/application-manage/{announcementId}/detail")
    public ResponseEntity<ApplicationManagePrepareDetailResponse> getPrepareDetail(
            @PathVariable Long announcementId
    ) {
        return ResponseEntity.ok(queryService.getDetail(announcementId));
    }
}
