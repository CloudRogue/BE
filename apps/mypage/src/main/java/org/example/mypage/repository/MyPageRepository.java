package org.example.mypage.repository;

import org.example.mypage.domain.Profile;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface MyPageRepository extends JpaRepository<Profile, Long> {
    Optional<Profile> findByUserIdAndDeletedAtIsNull(String userId);
}
