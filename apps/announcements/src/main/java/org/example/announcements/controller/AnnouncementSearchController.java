package org.example.announcements.controller;

import lombok.RequiredArgsConstructor;
import org.example.announcements.api.AnnouncementSort;
import org.example.announcements.api.ApiListResponse;
import org.example.announcements.dto.AnnouncementOpenItemResponse;
import org.example.announcements.service.AnnouncementListQueryService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/announcements/search")
public class AnnouncementSearchController {

    private final AnnouncementListQueryService listQueryService;

    // 전체 공고 목록 조회 (접수 전)
    @GetMapping("/upcoming")
    public ResponseEntity<ApiListResponse<AnnouncementOpenItemResponse>> getUpcoming(
            @RequestParam(required = false) String cursor,
            @RequestParam(required = false) Integer limit,
            @RequestParam(required = false) AnnouncementSort sort
    ) {
        int safeLimit = (limit == null) ? 0 : limit;
        return ResponseEntity.ok(listQueryService.getUpcoming(sort, cursor, safeLimit));
    }

    // 전체 공고 목록 조회 (접수 중)
    @GetMapping("/open")
    public ResponseEntity<ApiListResponse<AnnouncementOpenItemResponse>> getOpen(
            @RequestParam(required = false) String cursor,
            @RequestParam(required = false) Integer limit,
            @RequestParam(required = false) AnnouncementSort sort
    ) {
        int safeLimit = (limit == null) ? 0 : limit;
        return ResponseEntity.ok(listQueryService.getOpen(sort, cursor, safeLimit));
    }

    // 전체 공고 목록 조회 (마감)
    @GetMapping("/closed")
    public ResponseEntity<ApiListResponse<AnnouncementOpenItemResponse>> getClosed(
            @RequestParam(required = false) String cursor,
            @RequestParam(required = false) Integer limit
    ) {
        int safeLimit = (limit == null) ? 0 : limit;
        return ResponseEntity.ok(listQueryService.getClosed(cursor, safeLimit));
    }
}
