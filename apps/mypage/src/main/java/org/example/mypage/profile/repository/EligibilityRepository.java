package org.example.mypage.profile.repository;

import org.example.mypage.profile.domain.Eligibility;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface EligibilityRepository extends JpaRepository<Eligibility, Long> {
    List<Eligibility> findAllByRequiredOnboardingTrueOrderByIdAsc();
    List<Eligibility> findAllByRequiredOnboardingFalseOrderByIdAsc();


    long countByRequiredOnboardingTrue();

    @Query("""
        select e.id
        from Eligibility e
        where e.requiredOnboarding = true
    """)
    List<Long> findRequiredOnboardingIds();

    @Query("""
        select e.id
        from Eligibility e
        where e.requiredOnboarding = true
          and e.type in (org.example.mypage.profile.domain.enums.UiBlockType.BOOLEAN, org.example.mypage.profile.domain.enums.UiBlockType.SELECT_SINGLE)
    """)
    List<Long> findRequiredOnboardingBooleanOrSingleIds();

    List<Eligibility> findAllByIdInAndRequiredOnboardingFalseOrderByIdAsc(List<Long> ids);

    List<Eligibility> findAllByOrderByIdAsc();
}
