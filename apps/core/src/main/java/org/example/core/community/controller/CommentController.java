package org.example.core.community.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import lombok.RequiredArgsConstructor;
import org.example.core.community.dto.response.CommentSliceResponse;
import org.example.core.community.service.CommentService;
import org.springframework.http.RequestEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/community/")
public class CommentController {

    private final CommentService commentService;

    // 1. 댓글 목록 조회
    @Operation(
            summary = "공고 댓글 목록 조회",
            description = "특정 공고의 댓글을 커서 기반 페이징으로 조회합니다. 최신순(ID 역순)으로 정렬됩니다."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "조회 성공"),
            @ApiResponse(responseCode = "404", description = "존재하지 않는 공고 ID")
    })
    @GetMapping("announcements/{announcementId}/comments")
    public RequestEntity<List<CommentSliceResponse>> getComments(
            @Parameter(description = "공고 식별자", example = "ann_LH_20260109_001")
            @PathVariable String announcementId,

            @Parameter(description = "마지막으로 조회된 댓글 ID (첫 페이지 조회 시 비움)")
            @RequestParam( value = "cursor", required = false) Long cursor,

            @Parameter(description = "한 번에 가져올 댓글 개수", example = "10")
            @RequestParam( value = "limit", required = false, defaultValue = "10") Integer limit
    ) {
        commentService.getComments(announcementId, cursor, limit);
        return null;
    }

    // 2. 댓글 작성
    @PostMapping("announcements/{announcementId}/comments")
    public RequestEntity<Void> createComment(
            @PathVariable String announcementId) {
        return null;
    }

    // 3. 댓글 수정
    @PatchMapping("comments/{commentPk}")
    public RequestEntity<Void> updateComment(@PathVariable Long commentPk) {
        return null;
    }

    // 4. 댓글 삭제
    @DeleteMapping("comments/{commentPk}")
    public RequestEntity<Void> deleteComment(@PathVariable Long commentPk) {
        return null;
    }

    // 5. 댓글 좋아요
    @PostMapping("comments/{commentPk}/like")
    public RequestEntity<Void> likeComment(@PathVariable Long commentPk) {
        return null;
    }

    // 6. 댓글 좋아요 취소
    @DeleteMapping("comments/{commentPk}/like")
    public RequestEntity<Void> unlikeComment(@PathVariable Long commentPk) {
        return null;
    }

    // 7. 댓글 신고
    @PostMapping("comments/{commentPk}/report")
    public RequestEntity<Void> reportComment(@PathVariable Long commentPk) {
        return null;
    }
}
