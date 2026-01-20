package org.example.mypage.profile.repository;

import org.example.mypage.profile.domain.EligibilityAnswer;
import org.example.mypage.profile.dto.OnboardingAnswer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Set;

public interface EligibilityAnswerRepository extends JpaRepository<EligibilityAnswer, Long> {

            @Query(value = """
                SELECT
                    e.id                  AS profileId,
                    e.title               AS title,
                    e.type                AS type,
                    opt.options           AS options,   -- 없으면 NULL
                    a.value               AS value,
                    e.required_onboarding AS requiredOnboarding
                FROM eligibility_answer a
                JOIN eligibility e
                  ON e.id = a.eligibility_id
                LEFT JOIN (
                    SELECT
                        o.eligibility_id,
                        ARRAY_AGG(o.label ORDER BY o.label) AS options
                    FROM eligibility_option o
                    GROUP BY o.eligibility_id
                ) opt
                  ON opt.eligibility_id = e.id
                WHERE a.user_id = :userId
                ORDER BY e.title ASC
            """, nativeQuery = true)
        List<OnboardingAnswer> findAllByUserId(@Param("userId") String userId);

        List<EligibilityAnswer> findAllByUserIdAndEligibilityIdIn(String userId, Set<Long> idSet);

}
