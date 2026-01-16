package org.example.announcements.controller;

import lombok.RequiredArgsConstructor;
import org.example.announcements.api.AnnouncementSort;
import org.example.announcements.api.ApiListResponse;
import org.example.announcements.dto.AnnouncementOpenItemResponse;
import org.example.announcements.exception.BusinessException;
import org.example.announcements.exception.ErrorCode;
import org.example.announcements.service.AnnouncementListQueryService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/announcements/search")
public class AnnouncementSearchController {

    private final AnnouncementListQueryService listQueryService;

    @Value("${announcements.paging.default-limit}")
    private int defaultLimit;

    @Value("${announcements.paging.max-limit}")
    private int maxLimit;

    // 전체 공고 목록 조회 (접수 전)
    @GetMapping("/upcoming")
    public ResponseEntity<ApiListResponse<AnnouncementOpenItemResponse>> getUpcoming(
            @RequestParam(required = false) String cursor,
            @RequestParam(required = false) Integer limit,
            @RequestParam(required = false) AnnouncementSort sort
    ) {
        int validatedLimit = requireValidLimit(limit);
        return ResponseEntity.ok(listQueryService.getUpcoming(sort, cursor, validatedLimit));
    }

    // 전체 공고 목록 조회 (접수 중)
    @GetMapping("/open")
    public ResponseEntity<ApiListResponse<AnnouncementOpenItemResponse>> getOpen(
            @RequestParam(required = false) String cursor,
            @RequestParam(required = false) Integer limit,
            @RequestParam(required = false) AnnouncementSort sort
    ) {
        int validatedLimit = requireValidLimit(limit);
        return ResponseEntity.ok(listQueryService.getOpen(sort, cursor, validatedLimit));
    }

    // 전체 공고 목록 조회 (마감)
    @GetMapping("/closed")
    public ResponseEntity<ApiListResponse<AnnouncementOpenItemResponse>> getClosed(
            @RequestParam(required = false) String cursor,
            @RequestParam(required = false) Integer limit
    ) {
        int validatedLimit = requireValidLimit(limit);
        return ResponseEntity.ok(listQueryService.getClosed(cursor, validatedLimit));
    }


    private int requireValidLimit(Integer limit) {
        if (limit == null) {
            throw new BusinessException(ErrorCode.INVALID_LIMIT,"limit 파라미터는 필수입니다.");
        }
        if (limit < 1) {
            throw new BusinessException(ErrorCode.INVALID_LIMIT,"limit은 1 이상이어야 합니다.");
        }
        if (limit > maxLimit) {
            throw new BusinessException(ErrorCode.INVALID_LIMIT, "limit은 " + maxLimit + " 이하여야 합니다.");
        }
        return limit;
    }
}
