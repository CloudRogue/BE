package org.example.core.community.controller;

import lombok.RequiredArgsConstructor;
import org.example.core.community.service.CommentService;
import org.springframework.http.RequestEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/community/")
public class CommentController {

    private final CommentService commentService;

    // 1. 댓글 목록 조회
    @GetMapping("announcements/{announcementId}/comments")
    public RequestEntity<Void> getComments(@PathVariable String announcementId) {
        return null;
    }

    // 2. 댓글 작성
    @PostMapping("announcements/{announcementId}/comments")
    public RequestEntity<Void> createComment(@PathVariable String announcementId) {
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
