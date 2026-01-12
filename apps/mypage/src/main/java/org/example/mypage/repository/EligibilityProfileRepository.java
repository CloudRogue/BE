package org.example.mypage.repository;

import org.example.mypage.domain.EligibilityProfile;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface EligibilityProfileRepository extends JpaRepository<EligibilityProfile, Long> {
    Optional<EligibilityProfile> findById(Long id);
}
