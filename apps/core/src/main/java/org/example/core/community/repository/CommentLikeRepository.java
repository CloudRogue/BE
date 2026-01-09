package org.example.core.community.repository;

import org.example.core.community.domain.CommentLike;
import org.example.core.community.dto.CommentCountDto;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface CommentLikeRepository extends JpaRepository<CommentLike, Long> {

    @Query("SELECT l.comment.id AS commentId, COUNT(l) AS count " +
            "FROM CommentLike l " +
            "WHERE l.comment.id IN :commentIds " +
            "GROUP BY l.comment.id")
    List<CommentCountDto> countLikesByCommentIds(@Param("commentIds") List<Long> commentIds);
}
