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

        // 1. ScrollPosition 설정 (커서가 없으면 initial, 있으면 keyset 생성)
        // KeysetScrollPosition은 마지막으로 본 데이터의 식별자(ID)를 기억합니다.
        ScrollPosition position = (cursor == null)
                ? ScrollPosition.keyset()
                : ScrollPosition.of(Map.of("id", cursor), ScrollPosition.Direction.BACKWARD);

        // 2. Scrolling 쿼리 실행
        Window<Comment> window = commentRepo.findFirstByAnnouncementIdOrderByIdDesc(
                announcementId,
                position,
                Limit.of(limit)
        );

        List<Comment> resultComments = window.getContent();

        // 3. 일괄 카운트 및 조회
        List<Long> commentIds = resultComments.stream().map(Comment::getId).toList();

        Map<Long, Long> likeCountMap = commentLikeRepo.countLikesByCommentIds(commentIds).stream()
                .collect(Collectors.toMap(
                    CommentCountDto::getCommentId,
                    CommentCountDto::getCount
                ));

        Map<Long, Long> reportCountMap = commentReportRepo.countReportsByCommentIds(commentIds).stream()
                .collect(Collectors.toMap(
                    CommentCountDto::getCommentId,
                    CommentCountDto::getCount
                ));

        // 4. DTO 변환
        List<CommentContentResponse> contents = resultComments.stream()
                .map(comment -> CommentContentResponse.of(
                        comment,
                        likeCountMap.getOrDefault(comment.getId(), 0L),
                        reportCountMap.getOrDefault(comment.getId(), 0L)
                ))
                .toList();

        // 5. 다음 커서 추출 (Window가 다음 위치를 알고 있음)
        Long nextCursor = null;
        if (!window.isLast()) {
            // KeysetScrollPosition에서 ID 값을 추출
            ScrollPosition nextPos = window.positionAt(resultComments.getLast());
            if (nextPos instanceof KeysetScrollPosition keyset) {
                nextCursor = (Long) keyset.getKeys().get("id");
            }
        }

        return new CommentSliceResponse(contents, nextCursor, !window.isLast());
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
