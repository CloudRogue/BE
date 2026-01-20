package org.example.announcements.repository;

import org.example.announcements.domain.AnnouncementRegion;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AnnouncementRegionRepository extends JpaRepository<AnnouncementRegion, Long> {

    // 공고 기준으로 지역 조회
    List<AnnouncementRegion> findAllByAnnouncement_Id(Long announcementId);


}
