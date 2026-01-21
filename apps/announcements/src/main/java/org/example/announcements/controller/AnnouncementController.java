package org.example.announcements.controller;


import jakarta.validation.constraints.Min;
import lombok.RequiredArgsConstructor;
import org.example.announcements.api.OutboundType;
import org.example.announcements.dto.internal.mypage.MypageOutboundRequest;
import org.example.announcements.dto.internal.mypage.MypageScrapRequest;
import org.example.announcements.exception.BusinessException;
import org.example.announcements.exception.ErrorCode;
import org.example.announcements.repository.AnnouncementRepository;
import org.example.announcements.service.AnnouncementApplyCommandService;
import org.example.announcements.service.internal.mypage.MypageClient;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@Validated
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/announcements")
public class AnnouncementController {

    private final AnnouncementRepository announcementRepository;
    private final AnnouncementApplyCommandService applyCommandService;

    //마이페이지 내부 api 호출용
    private final MypageClient mypageClient;

    //공고 열람/외부이동 기록
    @PostMapping("/{announcementId}/outbounds")
    public ResponseEntity<Void> postOutbound(
            @AuthenticationPrincipal(expression = "userId") String userId,
            @PathVariable  Long announcementId,
            @RequestParam(required = false) OutboundType type
    ) {
        // type 기본값 VIEW
        if (type == null) type = OutboundType.VIEW;

        if (userId == null || userId.isBlank()) {
            throw new BusinessException(ErrorCode.INVALID_REQUEST, "userId가 필요합니다.");
        }
        announcementRepository.findById(announcementId)
                .orElseThrow(() -> new BusinessException(ErrorCode.ANNOUNCEMENT_NOT_FOUND));


        mypageClient.postOutbound(new MypageOutboundRequest(userId, announcementId, type));

        // 204
        return ResponseEntity.noContent().build();
    }

    // 지원관리 담기
    @PostMapping("/{announcementId}/apply")
    public ResponseEntity<Void> apply(
            @AuthenticationPrincipal(expression = "userId") String userId,
            @PathVariable  Long announcementId
    ) {
        applyCommandService.apply(userId, announcementId);
        return ResponseEntity.noContent().build();
    }

    // 공고 찜 추가
    @PostMapping("/{announcementId}/scrap")
    public ResponseEntity<Void> postScrap(
            @AuthenticationPrincipal(expression = "userId") String userId,
            @PathVariable  Long announcementId
    ) {
        if (userId == null || userId.isBlank()) {
            throw new BusinessException(ErrorCode.INVALID_REQUEST, "userId가 필요합니다.");
        }
        announcementRepository.findById(announcementId)
                .orElseThrow(() -> new BusinessException(ErrorCode.ANNOUNCEMENT_NOT_FOUND));

        mypageClient.postScrap(new MypageScrapRequest(userId, announcementId));

        return ResponseEntity.noContent().build();
    }

    // 공고 찜 해제
    @DeleteMapping("/{announcementId}/scrap")
    public ResponseEntity<Void> deleteScrap(
            @AuthenticationPrincipal(expression = "userId") String userId,
            @PathVariable  Long announcementId
    ) {
        if (userId == null || userId.isBlank()) {
            throw new BusinessException(ErrorCode.INVALID_REQUEST, "userId가 필요합니다.");
        }

        announcementRepository.findById(announcementId)
                .orElseThrow(() -> new BusinessException(ErrorCode.ANNOUNCEMENT_NOT_FOUND));

        mypageClient.deleteScrap(new MypageScrapRequest(userId, announcementId));

        return ResponseEntity.noContent().build();
    }
}
