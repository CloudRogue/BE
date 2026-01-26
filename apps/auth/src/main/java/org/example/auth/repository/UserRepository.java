package org.example.auth.repository;

import org.example.auth.domain.OAuthProvider;
import org.example.auth.domain.Users;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepository extends JpaRepository<Users, Long> {
    Optional<Users> findByProviderAndProviderUserId(OAuthProvider provider, String providerUserId);

    Optional<Users> findByUserId(String userId);
}
