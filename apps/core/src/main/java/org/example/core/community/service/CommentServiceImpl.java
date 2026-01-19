package org.example.core.community.service;

import lombok.RequiredArgsConstructor;
import org.example.core.community.domain.Comment;
import org.example.core.community.dto.CommentCountDto;
import org.example.core.community.dto.response.CommentContentResponse;
import org.example.core.community.dto.response.CommentSliceResponse;
import org.example.core.community.repository.CommentLikeRepository;
import org.example.core.community.repository.CommentReportRepository;
import org.example.core.community.repository.CommentRepository;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CommentServiceImpl implements CommentService {

    private final CommentRepository commentRepo;
    private final CommentLikeRepository commentLikeRepo;
    private final CommentReportRepository commentReportRepo;

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
    public Long createComment(String announcementId, CommentCreateRequest request, String authorId) {
        Comment comment;

        // userID 향후에 추가?
        if (request.parentId() == null) {
            // 1. 원 댓글 생성
            comment = Comment.newParentComment(
                    announcementId,
                    authorId,
                    request.content()
            );
        } else {
            // 2. 대댓글 생성
            Comment parent = commentRepo.findById(request.parentId())
                    .orElseThrow(() -> new EntityNotFoundException("부모 댓글을 찾을 수 없습니다."));

            // 부모 댓글이 해당 공고의 것이 맞는지 확인
            if (!parent.getAnnouncementId().equals(announcementId)) {
                throw new IllegalArgumentException("공고 ID가 일치하지 않는 부모 댓글입니다.");
            }

            comment = Comment.newKindAnswer(
                    announcementId,
                    parent,
                    authorId,
                    request.content()
            );
        }

        return commentRepo.save(comment).getId();
    }

    @Override
    public void updateComment() {

    }

    @Override
    public void deleteComment() {

    }
}
