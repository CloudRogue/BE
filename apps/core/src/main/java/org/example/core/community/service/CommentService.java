package org.example.core.community.service;

import org.example.core.community.dto.request.CommentCreateRequest;
import org.example.core.community.dto.response.CommentSliceResponse;
import org.example.core.community.dto.response.CommentUpdateResponse;

public interface CommentService {

    // 1. 댓글 목록 조회
    CommentSliceResponse getComments(String announcementId, Long cursor, Integer limit);
    // 2. 댓글 작성
    Long createComment(String announcementId, String content, String authorId, Long parentId);
    // 3. 댓글 수정
    CommentUpdateResponse updateComment(Long commentPk, String content, String user); 
    // 4. 댓글 삭제
    Long deleteComment(Long commentPk, String userId);
}
