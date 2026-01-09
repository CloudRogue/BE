package org.example.core.community.repository;

import org.example.core.community.domain.Comment;
import org.springframework.data.domain.Limit;
import org.springframework.data.domain.ScrollPosition;
import org.springframework.data.domain.Window;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CommentRepository extends JpaRepository<Comment, Long> {

    Window<Comment> findFirstByAnnouncementIdOrderByIdDesc(
            String announcementId,
            ScrollPosition position,
            Limit limit
    );
}