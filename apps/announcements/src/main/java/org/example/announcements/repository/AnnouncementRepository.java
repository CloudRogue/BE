package org.example.announcements.repository;

import org.example.announcements.domain.Announcement;
import org.example.announcements.domain.AnnouncementSource;
import org.springframework.data.domain.*;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.Optional;

@Repository
public interface AnnouncementRepository extends JpaRepository<Announcement, Long> {

    //파싱서버에서 넘어온 공고를 db에 저장하기 위해 사용
    Optional<Announcement> findBySourceAndExternalKey(AnnouncementSource source, String externalKey);

    // 접수중(open) + 최신순(createdAt desc, id desc)
    Window<Announcement> findByStartDateLessThanEqualAndEndDateGreaterThanEqualOrderByCreatedAtDescIdDesc(
            LocalDate today1,
            LocalDate today2,
            KeysetScrollPosition position,
            Limit limit
    );

    // 접수중(open) + 마감임박순(endDate asc, id desc)
    Window<Announcement> findByStartDateLessThanEqualAndEndDateGreaterThanEqualOrderByEndDateAscIdDesc(
            LocalDate today1,
            LocalDate today2,
            KeysetScrollPosition position,
            Limit limit
    );


}
