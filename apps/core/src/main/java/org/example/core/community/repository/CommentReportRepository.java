package org.example.core.community.repository;

import org.example.core.community.domain.CommentReport;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface CommentReportRepository extends JpaRepository<CommentReport, Long> {

    @Query("SELECT r.comment.id, COUNT(r) FROM CommentReport r " +
            "WHERE r.comment.id IN :commentIds " +
            "GROUP BY r.comment.id")
    List<Object[]> countReportsByCommentIds(@Param("commentIds") List<Long> commentIds);
}
