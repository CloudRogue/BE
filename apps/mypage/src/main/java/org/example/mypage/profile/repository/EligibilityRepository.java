package org.example.mypage.profile.repository;

import org.example.mypage.profile.domain.Eligibility;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface EligibilityRepository extends JpaRepository<Eligibility, Long> {
    List<Eligibility> findAllByRequiredOnboardingTrueOrderByIdAsc();
    List<Eligibility> findAllByRequiredOnboardingFalseOrderByIdAsc();
}
