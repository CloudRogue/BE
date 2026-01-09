package org.example.core.community.service;

import lombok.RequiredArgsConstructor;
import org.example.core.community.domain.Comment;
import org.example.core.community.dto.response.CommentListResponse;
import org.example.core.community.dto.response.CommentSliceResponse;
import org.example.core.community.repository.CommentLikeRepository;
import org.example.core.community.repository.CommentRepository;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
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

    @Override
    @Transactional(readOnly =true)
    public CommentSliceResponse getComments(String announcementId, Long cursor, Integer limit) {

        // 1. 커서 기반 조회
        Pageable pageable = PageRequest.of(0, limit + 1);

        List<Comment> comments = (cursor == null)
                ? commentRepo.findAllByAnnouncementIdOrderByIdDesc(announcementId, pageable)
                : commentRepo.findAllByAnnouncementIdAndIdLessThanOrderByIdDesc(announcementId, cursor, pageable);

        boolean hasNext = comments.size() > limit;
        List<Comment> resultComments = hasNext ? comments.subList(0, limit) : comments;

        // 2. 좋아요 카운트 일괄 조회
        List<Long> commentIds = resultComments.stream().map(Comment::getId).toList();

        // {댓글ID: 좋아요수} 형태의 Map으로 변환
        Map<Long, Long> likeCountMap = commentLikeRepo.countLikesByCommentIds(commentIds).stream()
                .collect(Collectors.toMap(
                        row -> (Long) row[0], // commentId
                        row -> (Long) row[1]  // count
                ));

        // 3. DTO 변환
        List<CommentListResponse> contents = resultComments.stream()
                .map(comment -> CommentListResponse.of(
                        comment,
                        likeCountMap.getOrDefault(comment.getId(), 0L), // 좋아요수 매핑
                        0L // 리포트 카운트는 아직 0
                ))
                .toList();

        // 다음 커서 계산
        Long nextCursor = resultComments.isEmpty() ? null
                : resultComments.get(resultComments.size() - 1).getId();

        return new CommentSliceResponse(contents, nextCursor, hasNext);
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
