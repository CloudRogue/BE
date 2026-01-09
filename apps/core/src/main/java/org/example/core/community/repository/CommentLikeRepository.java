package org.example.core.community.repository;

import org.example.core.community.domain.CommentLike;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface CommentLikeRepository extends JpaRepository<CommentLike, Long> {

    @Query("SELECT l.comment.id, COUNT(l) FROM CommentLike l " +
            "WHERE l.comment.id IN :commentIds " +
            "GROUP BY l.comment.id")
    List<Object[]> countLikesByCommentIds(@Param("commentIds") List<Long> commentIds);

}
