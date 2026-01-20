package org.example.mypage.activity.repository;

import org.example.mypage.activity.domain.Scrap;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface ScrapRepository extends JpaRepository<Scrap, Long> {


    @Query("""
        select s
        from Scrap s
        where s.userId = :userId
          and (:cursor is null or s.id < :cursor)
        order by s.id desc
    """)
    List<Scrap> scrollByUserId(@Param("userId") String userId, @Param("cursor") Long cursor, Pageable pageable
    );

    int deleteByUserIdAndAnnouncementId(String userId, Long announcementId);
    boolean existsByUserIdAndAnnouncementId(String userId, Long announcementId);
}
