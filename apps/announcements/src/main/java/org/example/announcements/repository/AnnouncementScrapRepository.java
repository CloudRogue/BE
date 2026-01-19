package org.example.announcements.repository;

import org.example.announcements.domain.AnnouncementScrap;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AnnouncementScrapRepository extends JpaRepository<AnnouncementScrap, Long> {

    // 로그인 유저 기준 찜 여부 체크
    boolean existsByUserIdAndAnnouncement_Id(String userId, Long announcementId);
}
