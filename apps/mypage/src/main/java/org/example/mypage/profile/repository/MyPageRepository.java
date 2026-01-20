package org.example.mypage.profile.repository;

import org.example.mypage.profile.domain.Profile;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface MyPageRepository extends JpaRepository<Profile, Long> {
    Optional<Profile> findByUserIdAndDeletedAtIsNull(String userId);
}
