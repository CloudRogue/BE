package org.example.announcements.repository;

import org.example.announcements.domain.AnnouncementApplication;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AnnouncementApplicationRepository extends JpaRepository<AnnouncementApplication, Long> ,AnnouncementApplicationManageQueryRepository{

    //중복 지원 방어 체크용
    boolean existsByUserIdAndAnnouncementId(String userId, Long announcementId);
}
