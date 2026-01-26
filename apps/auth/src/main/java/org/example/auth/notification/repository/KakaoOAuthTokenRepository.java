package org.example.auth.notification.repository;

import org.example.auth.notification.domain.KakaoOAuthToken;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface KakaoOAuthTokenRepository extends JpaRepository<KakaoOAuthToken, String> {

}
