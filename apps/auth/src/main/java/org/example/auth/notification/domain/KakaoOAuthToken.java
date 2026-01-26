package org.example.auth.notification.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "kakao_oauth_tokens")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class KakaoOAuthToken {

    @Id
    @Column(name = "user_id", nullable = false)
    private String userId;

    //암호화된 리프레쉬 토큰
    @Column(name = "refresh_token_enc", nullable = false)
    private String refreshTokenEnc;

    @Column(name = "refresh_expires_at")
    private LocalDateTime refreshExpiresAt;

    @UpdateTimestamp
    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    public static KakaoOAuthToken create(String userId, String refreshTokenEnc, LocalDateTime refreshExpiresAt) {
        KakaoOAuthToken t = new KakaoOAuthToken();
        t.userId = userId;
        t.refreshTokenEnc = refreshTokenEnc;
        t.refreshExpiresAt = refreshExpiresAt;
        return t;
    }

    public void update(String refreshTokenEnc, LocalDateTime refreshExpiresAt) {
        this.refreshTokenEnc = refreshTokenEnc;
        this.refreshExpiresAt = refreshExpiresAt;
    }
}
