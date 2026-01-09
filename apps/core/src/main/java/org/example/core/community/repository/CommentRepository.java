package org.example.core.community.repository;

import org.example.core.community.domain.Comment;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CommentRepository extends JpaRepository<Comment, Long> {

    // 첫 페이지 조회 (커서 없음)
    @Query("SELECT c FROM Comment c " +
            "WHERE c.announcementId = :announcementId " +
            "ORDER BY c.id DESC")
    List<Comment> findAllByAnnouncementIdOrderByIdDesc(
            @Param("announcementId") String announcementId,
            Pageable pageable
    );

    // 다음 페이지 조회 (커서 존재)
    @Query("SELECT c FROM Comment c " +
            "WHERE c.announcementId = :announcementId " +
            "AND c.id < :cursor " +
            "ORDER BY c.id DESC")
    List<Comment> findAllByAnnouncementIdAndIdLessThanOrderByIdDesc(
            @Param("announcementId") String announcementId,
            @Param("cursor") Long cursor,
            Pageable pageable
    );
}