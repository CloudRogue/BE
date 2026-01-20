package org.example.mypage.profile.repository;

import org.example.mypage.profile.domain.EligibilityOption;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface EligibilityOptionRepository extends JpaRepository<EligibilityOption, Long> {

    @Query("""
        select o
        from EligibilityOption o
        where o.eligibility.id in :eligibilityIds
        order by o.eligibility.id asc, o.displayOrder asc
    """)
    List<EligibilityOption> findAllByEligibilityIdsOrderByEligibilityIdAndDisplayOrder(@Param("eligibilityIds") List<Long> eligibilityIds);



}
