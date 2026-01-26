package org.example.announcements.repository;

import org.example.announcements.domain.AnnouncementRegion;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AnnouncementRegionRepository extends JpaRepository<AnnouncementRegion, Long> {

    // 공고 기준으로 지역 조회
    List<AnnouncementRegion> findAllByAnnouncement_Id(Long announcementId);

    boolean existsByAnnouncement_IdAndRegionName(Long announcementId, String regionName);


    @Query("select ar.regionName " +
            "from AnnouncementRegion ar " +
            "where ar.announcement.id = :announcementId")
    List<String> findRegionNamesByAnnouncementId(@Param("announcementId") Long announcementId);
}
