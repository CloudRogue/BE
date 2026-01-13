package org.example.auth.Controller;

import com.github.f4b6a3.ulid.UlidCreator;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.security.oauth2.jwt.JwtClaimsSet;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.JwtEncoderParameters;
import org.springframework.web.bind.annotation.*;
import java.time.Instant;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/local")
public class DevController {

    private static final String ACCESS_COOKIE_NAME = "ACCESS_TOKEN";
    private static final String REFRESH_COOKIE_NAME = "REFRESH_TOKEN";

    private final JwtEncoder jwtEncoder;

    @Value("${jwt.issuer}")
    private String issuer;

    @Value("${jwt.access-token-expire-seconds}")
    private long accessExpireSeconds;

    @Value("${jwt.refresh-token-expire-seconds}")
    private long refreshExpireSeconds;

    /**
     * 로컬에서만: 임의 유저로 access/refresh JWT 발급 후 HttpOnly 쿠키 세팅
     * - 토큰은 바디로 내려주지 않음 (cookie only)
     */
    @GetMapping("/auth")
    public ResponseEntity<LocalAuthResponse> auth(HttpServletResponse response) {
        String userId = UlidCreator.getUlid().toString();

        String accessToken = issueAccessToken(userId);
        String refreshToken = issueRefreshToken(userId);

        // access cookie
        response.addHeader(HttpHeaders.SET_COOKIE, ResponseCookie.from(ACCESS_COOKIE_NAME, accessToken)
                .httpOnly(true)
                .secure(false)
                .sameSite("Lax")
                .path("/")
                .maxAge(accessExpireSeconds)
                .build()
                .toString()
        );

        // refresh cookie
        response.addHeader(HttpHeaders.SET_COOKIE, ResponseCookie.from(REFRESH_COOKIE_NAME, refreshToken)
                .httpOnly(true)
                .secure(false)
                .sameSite("Lax")
                .path("/")
                .maxAge(refreshExpireSeconds)
                .build()
                .toString()
        );

        return ResponseEntity.ok(new LocalAuthResponse(userId));
    }

    private String issueAccessToken(String userId) {
        Instant now = Instant.now();

        JwtClaimsSet claims = JwtClaimsSet.builder()
                .issuer(issuer)
                .issuedAt(now)
                .expiresAt(now.plusSeconds(accessExpireSeconds))
                .subject(userId)
                .claim("userId", userId)
                .claim("roles", List.of("ROLE_USER"))
                .claim("tokenType", "ACCESS")
                .build();

        return jwtEncoder.encode(JwtEncoderParameters.from(claims)).getTokenValue();
    }

    private String issueRefreshToken(String userId) {
        Instant now = Instant.now();

        JwtClaimsSet claims = JwtClaimsSet.builder()
                .issuer(issuer)
                .issuedAt(now)
                .expiresAt(now.plusSeconds(refreshExpireSeconds))
                .subject(userId)
                .claim("userId", userId)
                .claim("tokenType", "REFRESH")
                .build();

        return jwtEncoder.encode(JwtEncoderParameters.from(claims)).getTokenValue();
    }

    public record LocalAuthResponse(String userId) {}
}
