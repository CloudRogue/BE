package org.example.core.community.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
//import org.example.auth.dto.UsersPrincipal;
import org.example.core.community.dto.request.CommentCreateRequest;
import org.example.core.community.dto.request.CommentUpdateRequest;
import org.example.core.community.dto.response.CommentSliceResponse;
import org.example.core.community.dto.response.CommentUpdateResponse;
import org.example.core.community.service.CommentService;
import org.springframework.http.HttpStatus;
import org.springframework.http.RequestEntity;
import org.springframework.http.ResponseEntity;
//import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/community")
public class CommentController {

    private final CommentService commentService;

    // 1. 댓글 목록 조회
    @GetMapping("/announcements/{announcementId}/comments")
    public ResponseEntity<CommentSliceResponse> getComments(
            @PathVariable String announcementId,
            @RequestParam( value = "cursor", required = false) Long cursor,
            @RequestParam( value = "limit", required = false, defaultValue = "10") Integer limit
    ) {
        CommentSliceResponse response = commentService.getComments(announcementId, cursor, limit);
        return ResponseEntity.ok(response);
    }

    // 2. 댓글 작성
    @PostMapping("/announcements/{announcementId}/comments")
    public ResponseEntity<Map<String, Long>> createComment(
            @PathVariable String announcementId,
            @RequestBody @Valid CommentCreateRequest request
//            @AuthenticationPrincipal UsersPrincipal user
            ) {
        String tempUserName = "tempUserName";
        Long commentId = commentService.createComment(announcementId, request.content(), tempUserName, request.parentId());
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(Map.of("commentId", commentId));
    }

    // 3. 댓글 수정
    @PatchMapping("/comments/{commentPk}")
    public ResponseEntity<CommentUpdateResponse> updateComment(
            @PathVariable Long commentPk,
            @RequestBody CommentUpdateRequest request
//            @AuthenticationPrincipal UsersPrincipal user
    ) {
        String tempUserName = "tempUserName";
        CommentUpdateResponse response = commentService.updateComment(commentPk, request.content(), tempUserName);

        return ResponseEntity.status(HttpStatus.OK).body(response);
    } 

    // 4. 댓글 삭제
    @DeleteMapping("comments/{commentPk}")
    public ResponseEntity<Void> deleteComment(
            @PathVariable Long commentPk
//            @AuthenticationPrincipal UsersPrincipal user
    ) {
        String tempUserName = "tempUserName";
        commentService.deleteComment(commentPk, tempUserName);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }
}
