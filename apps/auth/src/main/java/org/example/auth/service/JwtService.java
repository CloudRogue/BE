package org.example.auth.service;

import lombok.RequiredArgsConstructor;
import org.example.auth.dto.UsersPrincipal;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.oauth2.jose.jws.SignatureAlgorithm;
import org.springframework.security.oauth2.jwt.JwsHeader;
import org.springframework.security.oauth2.jwt.JwtClaimsSet;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.JwtEncoderParameters;
import org.springframework.stereotype.Service;

import java.time.Instant;

@Service
@RequiredArgsConstructor
public class JwtService {

    private final JwtEncoder jwtEncoder;

    @Value("${jwt.issuer}")
    private String issuer;

    @Value("${jwt.access-token-expire-seconds}")
    private long accessTokenExpireSeconds;

    @Value("${jwt.refresh-token-expire-seconds}")
    private long refreshTokenExpireSeconds;

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

        return jwtEncoder.encode(JwtEncoderParameters.from(jwsHeader, claims))
                .getTokenValue();
    }

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

        return jwtEncoder.encode(JwtEncoderParameters.from(jwsHeader, claims))
                .getTokenValue();
    }
}