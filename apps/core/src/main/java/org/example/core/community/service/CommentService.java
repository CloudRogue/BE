package org.example.core.community.service;

public interface CommentService {

    // 1. 댓글 목록 조회
    void getComments();
    // 2. 댓글 작성
    void createComment();
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
