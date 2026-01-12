package org.example.core.community.controller;

import com.sun.security.auth.UserPrincipal;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.example.core.community.dto.request.CommentCreateRequest;
import org.example.core.community.dto.response.CommentSliceResponse;
import org.example.core.community.service.CommentService;
import org.springframework.http.HttpStatus;
import org.springframework.http.RequestEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/community/")
public class CommentController {

    private final CommentService commentService;

    // 1. 댓글 목록 조회
    @GetMapping("announcements/{announcementId}/comments")
    public ResponseEntity<CommentSliceResponse> getComments(
            @PathVariable String announcementId,
            @RequestParam( value = "cursor", required = false) Long cursor,
            @RequestParam( value = "limit", required = false, defaultValue = "10") Integer limit
    ) {
        CommentSliceResponse response = commentService.getComments(announcementId, cursor, limit);
        return ResponseEntity.ok(response);
    }

    // 2. 댓글 작성
    @PostMapping("announcements/{announcementId}/comments")
    public ResponseEntity<Map<String, Long>> createComment(
            @PathVariable String announcementId,
            @RequestBody @Valid CommentCreateRequest request
            ) {
        Long commentId = commentService.createComment(announcementId, request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(Map.of("commentId", commentId));
    }

    // 3. 댓글 수정
    @PatchMapping("comments/{commentPk}")
    public ResponseEntity<Map<String, Long>> updateComment(
            @PathVariable Long commentPk,
            @RequestBody String content,
            String user // 향후 @AuthenticationPrincipal 주입
    ) {
        commentService.updateComment(commentPk, content, user);

        return ResponseEntity.status(HttpStatus.OK)
                .body(Map.of("commentId", commentPk));
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
