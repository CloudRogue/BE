package org.example.announcements.repository;

import org.example.announcements.domain.Announcement;
import org.springframework.data.domain.*;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;

public interface AnnouncementRepository extends JpaRepository<Announcement, Long> {

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
