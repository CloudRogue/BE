package org.example.announcements.controller;

import lombok.RequiredArgsConstructor;
import org.example.announcements.api.ApplicationManageListResponse;
import org.example.announcements.exception.BusinessException;
import org.example.announcements.exception.ErrorCode;
import org.example.announcements.service.ApplicationManageQueryService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import static org.example.announcements.exception.ErrorCode.UNAUTHORIZED;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/announcements/application-manage")
public class AnnouncementApplicationManageController {

    private final ApplicationManageQueryService applicationManageQueryService;

    @Value("${announcements.application-manage.size.min}")
    private int minSize;

    @Value("${announcements.application-manage.size.max}")
    private int maxSize;


    //신청관리 - 지원 완료 후에 진행중인 공고 목록 조회
    @GetMapping("/applied")
    public ResponseEntity<ApplicationManageListResponse> getApplied(
            @AuthenticationPrincipal Jwt jwt,
            @RequestParam(required = false) Long cursor,
            @RequestParam(required = false) Integer size
    ) {
        if (jwt == null) throw new BusinessException(UNAUTHORIZED, "비로그인/토큰 만료");
        String userId = jwt.getSubject();

        int validatedSize = requireValidSize(size);

        return ResponseEntity.ok(
                applicationManageQueryService.getApplying(userId, cursor, validatedSize)
        );
    }

    //신청관리 - 서류 발표대기
    @GetMapping("/document-pending")
    public ResponseEntity<ApplicationManageListResponse> getDocumentPending(
            @AuthenticationPrincipal  Jwt jwt,
            @RequestParam(required = false) Long cursor,
            @RequestParam(required = false) Integer size
    ) {
        if (jwt == null) throw new BusinessException(UNAUTHORIZED, "비로그인/토큰 만료");
        String userId = jwt.getSubject();
        int validatedSize = requireValidSize(size);

        return ResponseEntity.ok(
                applicationManageQueryService.getDocumentPending(userId, cursor, validatedSize)
        );
    }
    //신청관리 - 최종 발표대기
    @GetMapping("/final-pending")
    public ResponseEntity<ApplicationManageListResponse> getFinalPending(
            @AuthenticationPrincipal Jwt jwt,
            @RequestParam(required = false) Long cursor,
            @RequestParam(required = false) Integer size
    ) {
        if (jwt == null) throw new BusinessException(UNAUTHORIZED, "비로그인/토큰 만료");
        String userId = jwt.getSubject();
        int validatedSize = requireValidSize(size);

        return ResponseEntity.ok(
                applicationManageQueryService.getFinalPending(userId, cursor, validatedSize)
        );
    }

    //신청관리 - 발표마감
    @GetMapping("/closed")
    public ResponseEntity<ApplicationManageListResponse> getClosed(
            @AuthenticationPrincipal Jwt jwt,
            @RequestParam(required = false) Long cursor,
            @RequestParam(required = false) Integer size
    ) {
        if (jwt == null) throw new BusinessException(UNAUTHORIZED, "비로그인/토큰 만료");
        String userId = jwt.getSubject();
        int validatedSize = requireValidSize(size);

        return ResponseEntity.ok(
                applicationManageQueryService.getClosed(userId, cursor, validatedSize)
        );
    }

    //검증 유틸
    private int requireValidSize(Integer size) {
        if (size == null) {
            throw new BusinessException(ErrorCode.INVALID_LIMIT, "size 파라미터는 필수입니다.");
        }
        if (size < minSize) {
            throw new BusinessException(ErrorCode.INVALID_LIMIT, "size는 " + minSize + " 이상이어야 합니다.");
        }
        if (size > maxSize) {
            throw new BusinessException(ErrorCode.INVALID_LIMIT, "size는 " + maxSize + " 이하여야 합니다.");
        }
        return size;
    }
}
