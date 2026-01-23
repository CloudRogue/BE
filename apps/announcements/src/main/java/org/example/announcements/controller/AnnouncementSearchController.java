package org.example.announcements.controller;

import lombok.RequiredArgsConstructor;
import org.example.announcements.api.AnnouncementSort;
import org.example.announcements.api.ApiListResponse;
import org.example.announcements.api.PersonalizedSort;
import org.example.announcements.dto.AnnouncementOpenItemResponse;
import org.example.announcements.dto.AnnouncementSearchItemResponse;
import org.example.announcements.exception.BusinessException;
import org.example.announcements.exception.ErrorCode;
import org.example.announcements.service.AnnouncementListQueryService;
import org.example.announcements.service.PersonalizedAnnouncementQueryService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/announcements/search")
public class AnnouncementSearchController {

    private final AnnouncementListQueryService listQueryService;
    private final PersonalizedAnnouncementQueryService personalizedQueryService;

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

    // 공고 발행처 검색 (접수중 전용)
    @GetMapping("/publisher")
    public ResponseEntity<ApiListResponse<AnnouncementSearchItemResponse>> getOpenByPublisher(
            @RequestParam(required = false) String publisher,
            @RequestParam(required = false) String cursor,
            @RequestParam(required = false) Integer limit,
            @RequestParam(required = false) AnnouncementSort sort
    ) {
        // publisher 검증
        if (publisher == null || publisher.isBlank()) {
            throw new BusinessException(ErrorCode.INVALID_REQUEST, "publisher 파라미터는 필수입니다.");
        }
        String validatedPublisher = publisher.trim();

        int validatedLimit = requireValidLimit(limit);

        return ResponseEntity.ok(
                listQueryService.getOpenByPublisher(sort, validatedPublisher, cursor, validatedLimit)
        );
    }

    // 공고 주택 유형 검색 (접수중 전용)
    @GetMapping("/housing-type")
    public ResponseEntity<ApiListResponse<AnnouncementSearchItemResponse>> getOpenByHousingType(
            @RequestParam(required = false) String housingType,
            @RequestParam(required = false) String cursor,
            @RequestParam(required = false) Integer limit,
            @RequestParam(required = false) AnnouncementSort sort
    ) {
        // housingType 검증
        if (housingType == null ||housingType.isBlank()) {
            throw new BusinessException(ErrorCode.INVALID_REQUEST, "housingType 파라미터는 필수입니다.");
        }

        String validatedHousingType = housingType.trim();

        int validatedLimit = requireValidLimit(limit);

        return ResponseEntity.ok(
                listQueryService.getOpenByHousingType(sort, validatedHousingType, cursor, validatedLimit)
        );
    }

    @GetMapping("/region")
    public ResponseEntity<ApiListResponse<AnnouncementSearchItemResponse>> getOpenByRegion(
            @RequestParam(required = false) String regionName,
            @RequestParam(required = false) String cursor,
            @RequestParam(required = false) Integer limit,
            @RequestParam(required = false) AnnouncementSort sort
    ) {
        if (regionName == null || regionName.isBlank()) {
            throw new BusinessException(ErrorCode.INVALID_REQUEST, "regionName 파라미터는 필수입니다.");
        }

        int validatedLimit = requireValidLimit(limit);

        return ResponseEntity.ok(
                listQueryService.getOpenByRegion(
                        regionName.trim(),  // "강남구" 같
                        sort,
                        cursor,
                        validatedLimit
                )
        );
    }

    //맞춤 공고 목록 조회
    @GetMapping("/personalized")
    public ResponseEntity<ApiListResponse<AnnouncementSearchItemResponse>> getPersonalized(
            @AuthenticationPrincipal(expression = "userId") String userId, // ✅ 쿠키(JWT) 인증 기반
            @RequestParam(required = false) String cursor,
            @RequestParam(required = false) Integer limit,
            @RequestParam(required = false) PersonalizedSort sort
    ) {
        int validatedLimit = requireValidLimit(limit);

        return ResponseEntity.ok(
                personalizedQueryService.getPersonalized(
                        userId,
                        cursor,
                        validatedLimit,
                        sort
                )
        );
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
