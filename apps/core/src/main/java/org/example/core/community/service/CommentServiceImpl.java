package org.example.core.community.service;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.example.core.community.domain.Comment;
import org.example.core.community.dto.response.CommentContentResponse;
import org.example.core.community.dto.response.CommentSliceResponse;
import org.example.core.community.dto.response.CommentUpdateResponse;
import org.example.core.community.exception.UnAuthorizedCommentException;
import org.example.core.community.repository.CommentLikeRepository;
import org.example.core.community.repository.CommentReportRepository;
import org.example.core.community.repository.CommentRepository;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class CommentServiceImpl implements CommentService {

    private final CommentRepository commentRepo;

    @Override
    @Transactional(readOnly = true)
    public CommentSliceResponse getComments(String announcementId, Long cursor, Integer limit) {

        // 1. 전체 댓글 개수 조회 (totalPages 계산용)
        long totalElements = commentRepo.countByAnnouncementId(announcementId);

        // 2. ScrollPosition 설정
        ScrollPosition position = (cursor == null)
                ? ScrollPosition.keyset()
                : ScrollPosition.of(Map.of("id", cursor), ScrollPosition.Direction.FORWARD);

        // 3. 데이터 조회
        Window<Comment> window = commentRepo.findFirstByAnnouncementIdOrderByIdDesc(
                announcementId,
                position,
                Limit.of(limit)
        );

        List<Comment> resultComments = window.getContent();

        if (resultComments.isEmpty()) {
            return new CommentSliceResponse(List.of(), null, false, cursor != null, totalElements, limit);
        }

        // 4. DTO 변환
        List<CommentContentResponse> contents = resultComments.stream()
                .map(CommentContentResponse::of)
                .toList();

        // 5. 다음 커서 추출
        Long nextCursor = null;
        if (window.hasNext()) {
            ScrollPosition nextPos = window.positionAt(resultComments.getLast());
            if (nextPos instanceof KeysetScrollPosition keyset) {
                nextCursor = (Long) keyset.getKeys().get("id");
            }
        }

        // 6. 최종 응답 (totalElements와 limit을 넘겨 DTO 내부에서 계산)
        return new CommentSliceResponse(contents, nextCursor, window.hasNext(), cursor != null, totalElements, limit);
    }

    @Override
    @Transactional
    public Long createComment(String announcementId, String content, String authorId, Long parentId) {
        Comment comment;

        // 1. parentId가 없으면 -> '원 댓글' 생성
        if (parentId == null) {
            comment = Comment.newParentComment(
                    announcementId,
                    authorId,
                    content
            );
        }
        // 2. parentId가 있으면 -> '대댓글' 생성
        else {
            Comment parent = commentRepo.findById(parentId)
                    .orElseThrow(() -> new EntityNotFoundException("부모 댓글을 찾을 수 없습니다."));
            
            if (!parent.getAnnouncementId().equals(announcementId)) {
                throw new IllegalArgumentException("공고 ID가 일치하지 않는 부모 댓글입니다.");
            }
            if (parent.getDeletedAt() != null) {
                throw new IllegalStateException("삭제된 댓글에는 답글을 달 수 없습니다.");
            }
            comment = Comment.newKindAnswer(
                    announcementId,
                    parent,
                    authorId,
                    content
            );
        }
        return commentRepo.save(comment).getId();
    }

    @Override
    @Transactional
    public CommentUpdateResponse updateComment(Long commentPk, String content, String user) {

        Comment comment = commentRepo.findById(commentPk)
                .orElseThrow(() -> new EntityNotFoundException("댓글을 찾을 수 없습니다."));
        if(!comment.getAuthorUserId().equals(user)) {
            throw new UnAuthorizedCommentException("댓글 작성자만 수정할 수 있습니다.");
        }

        comment.updateContent(content);
        comment.touchUpdatedAt();
        
        return new CommentUpdateResponse(commentPk, comment.getUpdatedAt());
    } 

    @Override
    @Transactional
    public Long deleteComment(Long commentPk, String user) {
        Comment comment = commentRepo.findById(commentPk)
                .orElseThrow(() -> new EntityNotFoundException("댓글을 찾을 수 없습니다."));
        if(!comment.getAuthorUserId().equals(user)) {
            throw new UnAuthorizedCommentException("댓글 작성자만 삭제할 수 있습니다.");
        }
        comment.markSoftDeleted();

        return comment.getId();
    }
}
