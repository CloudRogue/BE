package org.example.mypage.profile.repository;


import org.example.mypage.profile.domain.AnnouncementEligibility;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface AnnouncementEligibilityRepository extends JpaRepository<AnnouncementEligibility, Long> {

    @Query("""
        select ae
        from AnnouncementEligibility ae
        join fetch ae.eligibility e
        where ae.announcementId = :announcementId
        order by ae.id asc
    """)
    List<AnnouncementEligibility> findAllByAnnouncementIdFetch(@Param("announcementId") long announcementId);

    @Query("""
        select ae
        from AnnouncementEligibility ae
        join fetch ae.eligibility e
        where e.requiredOnboarding = true
          and e.type in (org.example.mypage.profile.domain.enums.UiBlockType.BOOLEAN, org.example.mypage.profile.domain.enums.UiBlockType.SELECT_SINGLE)
    """)
    List<AnnouncementEligibility> findAllRequiredBoolOrSingleConditionsFetch();

    boolean existsByAnnouncementId(long announcementId);

    @Query("""
        select ae.eligibility.id
        from AnnouncementEligibility ae
        where ae.announcementId = :announcementId
    """)
    List<Long> findEligibilityIdsByAnnouncementId(@Param("announcementId") long announcementId);

}
