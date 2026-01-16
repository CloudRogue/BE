package org.example.announcements.repository;

import org.example.announcements.domain.AnnouncementSummary;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface AnnouncementSummaryRepository extends JpaRepository<AnnouncementSummary, Long> {

    //announcementId로 조회하기
    Optional<AnnouncementSummary> findByAnnouncementId(Long announcementId);
}
