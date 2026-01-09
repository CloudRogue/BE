package org.example.core.community.repository;

import org.example.core.community.domain.CommentLike;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface CommentLikeRepository extends JpaRepository<CommentLike, Long> {

    // 특정 댓글 ID 리스트를 넣어 각 댓글당 좋아요 개수를 그룹화해서 가져옴
    @Query("SELECT l.comment.id, COUNT(l) FROM CommentLike l " +
            "WHERE l.comment.id IN :commentIds " +
            "GROUP BY l.comment.id")
    List<Object[]> countLikesByCommentIds(@Param("commentIds") List<Long> commentIds);

}
