package org.example.announcements.controller;


import lombok.RequiredArgsConstructor;
import org.example.announcements.dto.AnnouncementOpenItemResponse;
import org.example.announcements.exception.BusinessException;
import org.example.announcements.service.AnnouncementApplyCommandService;
import org.example.announcements.service.AnnouncementSearchService;
import org.example.announcements.service.MypageActionService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static org.example.announcements.exception.ErrorCode.UNAUTHORIZED;

@Validated
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/announcements")
public class AnnouncementController {
    private static final int MIN_QUERY_LEN = 3;
    private static final int LIMIT = 20;

    private final MypageActionService mypageActionService;
    private final AnnouncementApplyCommandService applyCommandService;
    private final AnnouncementSearchService announcementSearchService;

    // 공고 열람기록
    @PostMapping("/{announcementId}/outbounds")
    public ResponseEntity<Void> postOutbound(
            @AuthenticationPrincipal Jwt jwt,
            @PathVariable Long announcementId
    ) {
        if (jwt == null) throw new BusinessException(UNAUTHORIZED, "비로그인/토큰 만료");
        String userId = jwt.getSubject();

        mypageActionService.recordOutbound(userId, announcementId);
        return ResponseEntity.noContent().build();
    }


    // 지원관리 담기
    @PostMapping("/{announcementId}/apply")
    public ResponseEntity<Void> apply(
            @AuthenticationPrincipal Jwt jwt,
            @PathVariable Long announcementId
    ) {
        if (jwt == null) throw new BusinessException(UNAUTHORIZED, "비로그인/토큰 만료");
        String userId = jwt.getSubject();

        applyCommandService.apply(userId, announcementId);
        return ResponseEntity.noContent().build();
    }

    // 공고 찜 추가
    @PostMapping("/{announcementId}/scrap")
    public ResponseEntity<Void> postScrap(
            @AuthenticationPrincipal Jwt jwt,
            @PathVariable Long announcementId
    ) {
        if (jwt == null) throw new BusinessException(UNAUTHORIZED, "비로그인/토큰 만료");
        String userId = jwt.getSubject();

        mypageActionService.addScrap(userId, announcementId);
        return ResponseEntity.noContent().build();
    }

    // 공고 찜 해제
    @DeleteMapping("/{announcementId}/scrap")
    public ResponseEntity<Void> deleteScrap(
            @AuthenticationPrincipal Jwt jwt,
            @PathVariable Long announcementId
    ) {
        if (jwt == null) throw new BusinessException(UNAUTHORIZED, "비로그인/토큰 만료");
        String userId = jwt.getSubject();
        mypageActionService.removeScrap(userId, announcementId);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/search")
    public ResponseEntity<List<AnnouncementOpenItemResponse>> search(
            @RequestParam("title") String title
    ) {
        return ResponseEntity.ok(announcementSearchService.searchByTitlePrefix(title));
    }
}


