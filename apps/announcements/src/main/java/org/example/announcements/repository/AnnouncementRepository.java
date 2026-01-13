package org.example.announcements.repository;

import org.example.announcements.domain.Announcement;
import org.example.announcements.domain.AnnouncementSource;
import org.springframework.data.domain.*;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.Optional;

public interface AnnouncementRepository extends JpaRepository<Announcement, Long> {

    //파싱서버에서 넘어온 공고를 db에 저장하기 위해 사용
    Optional<Announcement> findBySourceAndExternalKey(AnnouncementSource source, String externalKey);

    // 접수중(open) 공고 조회 + 최신순으로 조회하기
    @Query("""
        select a
        from Announcement a
        where a.startDate <= :today
          and a.endDate >= :today
        order by a.createdAt desc, a.id desc
    """)
    Window<Announcement> scrollOpenLatest(
            @Param("today") LocalDate today,
            KeysetScrollPosition position,
            Limit limit
    );

    // 접수중(open) 공고 조회 + 마감임박순으로 조회하기
    @Query("""
        select a
        from Announcement a
        where a.startDate <= :today
          and a.endDate >= :today
        order by a.endDate asc, a.id desc
    """)
    Window<Announcement> scrollOpenDeadline(
            @Param("today") LocalDate today,
            KeysetScrollPosition position,
            Limit limit
    );


}
