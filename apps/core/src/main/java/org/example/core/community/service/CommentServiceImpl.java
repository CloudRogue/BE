package org.example.core.community.service;

import lombok.RequiredArgsConstructor;
import org.example.core.community.domain.Comment;
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
    public void createComment() {

    }

    @Override
    public void updateComment() {

    }

    @Override
    public void deleteComment() {

    }

    @Override
    public void likeComment() {

    }

    @Override
    public void unlikeComment() {

    }

    @Override
    public void reportComment() {

    }
}
