package org.example.announcements.repository;

import org.example.announcements.domain.Announcement;
import org.springframework.data.domain.Limit;
import org.springframework.data.domain.ScrollPosition;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Window;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;

public interface AnnouncementRepository extends JpaRepository<Announcement, Long> {

    // 접수중(open) 공고 조회
    @Query("""
        select a
        from Announcement a
        where a.startDate <= :today
         and a.endDate >= :today
    """)
    Window<Announcement> scrollOpen(
            @Param("today")LocalDate today,
            Sort sort,
            ScrollPosition position,
            Limit limit
            );
}
