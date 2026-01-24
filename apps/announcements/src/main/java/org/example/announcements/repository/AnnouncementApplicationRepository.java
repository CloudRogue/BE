package org.example.announcements.repository;

import org.example.announcements.domain.AnnouncementApplication;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AnnouncementApplicationRepository extends JpaRepository<AnnouncementApplication, Long> ,AnnouncementApplicationManageQueryRepository{

    //중복 지원 방어 체크용
    boolean existsByUserIdAndAnnouncementId(String userId, Long announcementId);



    //특정공고에ㅔ 지원한 유저 id 조회 검수된 공고만
    @Query("""
        select aa.userId
        from AnnouncementApplication aa
        join Announcement a on a.id = aa.announcementId
        where aa.announcementId = :announcementId
          and a.adminChecked = true
    """)
    List<String> findUserIdsByAnnouncementIdChecked(Long announcementId);
}
