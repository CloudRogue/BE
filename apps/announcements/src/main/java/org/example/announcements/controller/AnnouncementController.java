package org.example.announcements.controller;


import lombok.RequiredArgsConstructor;
import org.example.announcements.service.AnnouncementApplyCommandService;
import org.example.announcements.service.MypageActionService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@Validated
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/announcements")
public class AnnouncementController {

    private final MypageActionService mypageActionService;
    private final AnnouncementApplyCommandService applyCommandService;

    // 공고 열람기록
    @PostMapping("/{announcementId}/outbounds")
    public ResponseEntity<Void> postOutbound(
            @AuthenticationPrincipal(expression = "userId") String userId,
            @PathVariable Long announcementId
    ) {
        mypageActionService.recordOutbound(userId, announcementId);
        return ResponseEntity.noContent().build();
    }

    // 지원관리 담기
    @PostMapping("/{announcementId}/apply")
    public ResponseEntity<Void> apply(
            @AuthenticationPrincipal(expression = "userId") String userId,
            @PathVariable Long announcementId
    ) {
        applyCommandService.apply(userId, announcementId);
        return ResponseEntity.noContent().build();
    }

    // 공고 찜 추가
    @PostMapping("/{announcementId}/scrap")
    public ResponseEntity<Void> postScrap(
            @AuthenticationPrincipal(expression = "userId") String userId,
            @PathVariable Long announcementId
    ) {
        mypageActionService.addScrap(userId, announcementId);
        return ResponseEntity.noContent().build();
    }

    // 공고 찜 해제
    @DeleteMapping("/{announcementId}/scrap")
    public ResponseEntity<Void> deleteScrap(
            @AuthenticationPrincipal(expression = "userId") String userId,
            @PathVariable Long announcementId
    ) {
        mypageActionService.removeScrap(userId, announcementId);
        return ResponseEntity.noContent().build();
    }
}
