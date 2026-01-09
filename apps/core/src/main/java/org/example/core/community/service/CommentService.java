package org.example.core.community.service;

import org.example.core.community.dto.request.CommentCreateRequest;
import org.example.core.community.dto.response.CommentSliceResponse;

public interface CommentService {

    // 1. 댓글 목록 조회
    CommentSliceResponse getComments(String announcementId, Long cursor, Integer limit);
    // 2. 댓글 작성
    Long createComment(String announcementId, CommentCreateRequest request);
    // 3. 댓글 수정
    void updateComment();
    // 4. 댓글 삭제
    void deleteComment();
    // 5. 댓글 좋아요
    void likeComment();
    // 6. 댓글 좋아요 취소
    void unlikeComment();
    // 7. 댓글 신고
    void reportComment();
}
