package org.example.auth.service;

import com.nimbusds.oauth2.sdk.TokenResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.auth.domain.RefreshToken;
import org.example.auth.domain.Users;
import org.example.auth.dto.TokenResponseDto;
import org.example.auth.dto.UsersPrincipal;
import org.example.auth.repository.RefreshTokenRepository;
import org.example.auth.repository.UserRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.jose.jws.SignatureAlgorithm;
import org.springframework.security.oauth2.jwt.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.Collections;

@Service
@Slf4j
@RequiredArgsConstructor
public class JwtService {

    private final JwtEncoder jwtEncoder;
    private final JwtDecoder jwtDecoder;
    private final RefreshTokenRepository refreshTokenRepository;
    private final UserRepository userRepository; // 사용자 정보 조회를 위해 추가

    @Value("${jwt.issuer}")
    private String issuer;

    @Value("${jwt.access-token-expire-seconds}")
    private long accessTokenExpireSeconds;

    @Value("${jwt.refresh-token-expire-seconds}")
    private long refreshTokenExpireSeconds;

    // --- 토큰 발급 로직 ---
    public String createAccessToken(UsersPrincipal principal) {
        Instant now = Instant.now();
        JwsHeader jwsHeader = JwsHeader.with(SignatureAlgorithm.RS256).build();

        JwtClaimsSet claims = JwtClaimsSet.builder()
                .issuer(issuer)
                .issuedAt(now)
                .expiresAt(now.plusSeconds(accessTokenExpireSeconds))
                .subject(principal.getUserId())
                .claim("nickname", principal.getNickname())
                .build();

        return jwtEncoder.encode(JwtEncoderParameters.from(jwsHeader, claims)).getTokenValue();
    }
    
    // --- 토큰 저장 로직 ---
    public String createRefreshToken(UsersPrincipal principal) {
        Instant now = Instant.now();
        JwsHeader jwsHeader = JwsHeader.with(SignatureAlgorithm.RS256).build();

        JwtClaimsSet claims = JwtClaimsSet.builder()
                .issuer(issuer)
                .issuedAt(now)
                .expiresAt(now.plusSeconds(refreshTokenExpireSeconds))
                .subject(principal.getUserId())
                .claim("type", "refresh")
                .build();

        return jwtEncoder.encode(JwtEncoderParameters.from(jwsHeader, claims)).getTokenValue();
    }

    @Transactional
    public void saveRefreshToken(UsersPrincipal principal, String refreshToken) {
        // 1. 만료 시간 계산 (설정값 사용)
        Instant expiryDate = Instant.now().plusSeconds(refreshTokenExpireSeconds);

        // 2. 이미 해당 유저의 토큰이 DB에 있는지 확인
        // 있으면 업데이트(Rotation), 없으면 새로 생성
        RefreshToken tokenEntity = refreshTokenRepository.findByUserId(principal.getUserId())
                .map(entity -> {
                    entity.updateToken(refreshToken, expiryDate); // 도메인의 updateToken 메서드 사용!
                    return entity;
                })
                .orElse(new RefreshToken(refreshToken, principal.getUserId(), expiryDate));

        // 3. 최종 저장
        refreshTokenRepository.save(tokenEntity);
    }
    

    // --- 리프레시 로직 (Rotation) ---
    @Transactional
    public TokenResponseDto refresh(String refreshToken) {
        // 1. 토큰 검증 및 유저 ID 추출
        Jwt jwt = jwtDecoder.decode(refreshToken);
        String userId = jwt.getSubject();

        // 2. DB 확인 (메서드 명은 findByUserId로 Repository에 추가되어 있어야 함)
        RefreshToken savedToken = refreshTokenRepository.findByUserId(userId)
                .filter(t -> t.getToken().equals(refreshToken))
                .orElseThrow(() -> new RuntimeException("유효하지 않은 토큰입니다."));

        // 추가: DB에 저장된 만료 시간 검증
        if (savedToken.getExpiryDate().isBefore(Instant.now())) {
            refreshTokenRepository.delete(savedToken); // 만료된 건 지워버림
            throw new RuntimeException("만료된 리프레시 토큰입니다.");
        }

        // 3. 사용자 정보 조회
        Users user = userRepository.findByUserId(userId)
                .orElseThrow(() -> new RuntimeException("사용자를 찾을 수 없습니다."));

        // 4. [기존 코드 훼손 없이 직접 생성]
        // 엔티티의 정보를 바탕으로 UsersPrincipal 객체를 직접 생성합니다.
        UsersPrincipal principal = new UsersPrincipal(
                user.getUserId(),
                user.getName(),
                Collections.singleton(new SimpleGrantedAuthority(user.getRole().name())),
                false // 리프레시 시점이므로 신규 가입자가 아님
        );

        // 5. 새로운 토큰 쌍 생성 및 DB 업데이트
        String newAccessToken = createAccessToken(principal);
        String newRefreshToken = createRefreshToken(principal);
        savedToken.updateToken(newRefreshToken, Instant.now().plusSeconds(refreshTokenExpireSeconds));

        return new TokenResponseDto(newAccessToken, newRefreshToken);
    }

    @Transactional
    public void logout(String refreshToken) {
        try {
            // 1. 토큰에서 유저 정보 추출 (검증 포함)
            Jwt jwt = jwtDecoder.decode(refreshToken);
            String userId = jwt.getSubject();

            // 2. 해당 유저의 리프레시 토큰을 DB에서 삭제
            refreshTokenRepository.deleteByUserId(userId);
        } catch (Exception e) {
            // 이미 만료되었거나 잘못된 토큰이어도 로그아웃은 진행되어야 하므로 예외 처리
            log.error("로그아웃 도중 토큰 삭제 실패: {}", e.getMessage());
        }
    }
}