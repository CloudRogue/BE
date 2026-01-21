package org.example.announcements.repository;

import org.example.announcements.domain.AnnouncementOverview;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface AnnouncementOverviewRepository extends JpaRepository<AnnouncementOverview, Long> {

    //공고로 개요 조회(없을 수 있음)
    Optional<AnnouncementOverview> findByAnnouncementId(Long announcementId);

    boolean existsByAnnouncementId(Long announcementId);

}
