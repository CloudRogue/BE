package org.example.auth.domain;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import java.time.Instant;

@Entity
@Getter
@NoArgsConstructor
public class RefreshToken {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String token;

    @Column(nullable = false, unique = true)
    private String userId;

    @Column(nullable = false)
    private Instant expiryDate;

    public RefreshToken(String token, String userId, Instant expiryDate) {
        this.token = token;
        this.userId = userId;
        this.expiryDate = expiryDate;
    }

    // 새로운 토큰으로 업데이트 (Rotation)
    public void updateToken(String newToken, Instant newExpiryDate) {
        this.token = newToken;
        this.expiryDate = newExpiryDate;
    }
}