package org.example.announcements.repository;

import org.example.announcements.domain.Announcement;
import org.example.announcements.domain.AnnouncementSource;
import org.springframework.data.domain.*;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface AnnouncementRepository extends JpaRepository<Announcement, Long>,AnnouncementRegionQueryRepository {

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

    // 접수전 공고 최신순
    Window<Announcement> findByStartDateGreaterThanOrderByCreatedAtDescIdDesc(
            LocalDate today,
            KeysetScrollPosition position,
            Limit limit
    );

    //접수전 마감임박순
    Window<Announcement> findByStartDateGreaterThanOrderByEndDateAscIdDesc(
            LocalDate today,
            KeysetScrollPosition position,
            Limit limit
    );

    // 마감 마감일 내림차순으로 정리
    Window<Announcement> findByEndDateLessThanOrderByEndDateDescIdDesc(
            LocalDate today,
            KeysetScrollPosition position,
            Limit limit
    );

    // 접수중인데 발행처부분일치하고 최신순으로
    Window<Announcement> findByStartDateLessThanEqualAndEndDateGreaterThanEqualAndPublisherContainingIgnoreCaseOrderByCreatedAtDescIdDesc(
            LocalDate today1,
            LocalDate today2,
            String publisher,
            KeysetScrollPosition position,
            Limit limit
    );


    // 접수중인데 발행처부분일치하고 마감임박순으로
    Window<Announcement> findByStartDateLessThanEqualAndEndDateGreaterThanEqualAndPublisherContainingIgnoreCaseOrderByEndDateAscIdDesc(
            LocalDate today1,
            LocalDate today2,
            String publisher,
            KeysetScrollPosition position,
            Limit limit
    );

    //접수중인데 주택유형검색으로 최신순으로
    Window<Announcement> findByStartDateLessThanEqualAndEndDateGreaterThanEqualAndHousingTypeContainingIgnoreCaseOrderByCreatedAtDescIdDesc(
            LocalDate today1,
            LocalDate today2,
            String housingType,
            KeysetScrollPosition position,
            Limit limit
    );


    //접수중인데 주택유형검색으로 마감임박순으로
    Window<Announcement> findByStartDateLessThanEqualAndEndDateGreaterThanEqualAndHousingTypeContainingIgnoreCaseOrderByEndDateAscIdDesc(
            LocalDate today1,
            LocalDate today2,
            String housingType,
            KeysetScrollPosition position,
            Limit limit
    );


    // 발행기관으로 필터
    @Query("""
        select distinct a.source
        from Announcement a
        where a.source is not null
    """)
    List<AnnouncementSource> findDistinctSources();

    // 주택유형으로 필터
    @Query("""
    select distinct trim(a.housingType)
    from Announcement a
    where a.housingType is not null and trim(a.housingType) <> ''
    order by trim(a.housingType) asc
""")
    List<String> findDistinctHousingTypes();


}
