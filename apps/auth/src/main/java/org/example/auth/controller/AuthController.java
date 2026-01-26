package org.example.auth.Controller;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.example.auth.dto.TokenResponseDto;
import org.example.auth.service.JwtService;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import java.time.Duration;


@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final JwtService jwtService; // 주입

    @PostMapping("/refresh")
    public ResponseEntity<Void> refresh(
            @CookieValue("REFRESH_TOKEN") String refreshToken, // 쿠키에서 리프레시 토큰 추출
            HttpServletResponse response
    ) {
        // 1. 서비스 로직 호출 (새 토큰 쌍 발급 및 DB 업데이트)
        TokenResponseDto tokenDto = jwtService.refresh(refreshToken);

        // 2. 새 토큰들을 다시 쿠키에 설정
        response.addHeader(HttpHeaders.SET_COOKIE, createCookie("ACCESS_TOKEN", tokenDto.accessToken(), "/").toString());
        response.addHeader(HttpHeaders.SET_COOKIE, createCookie("REFRESH_TOKEN", tokenDto.refreshToken(), "/api/auth").toString());

        return ResponseEntity.ok().build();
    }

    @PostMapping("/logout")
    public ResponseEntity<Void> logout(
            HttpServletResponse response,
            @CookieValue(value = "REFRESH_TOKEN", required = false) String refreshToken
    ) {
        // 1. DB에서 리프레시 토큰 삭제
        if (refreshToken != null) {
            jwtService.logout(refreshToken);
        }

        response.addHeader(HttpHeaders.SET_COOKIE, expire("ACCESS_TOKEN", "/").toString());
        response.addHeader(HttpHeaders.SET_COOKIE, expire("REFRESH_TOKEN", "/api/auth").toString());

        return ResponseEntity.noContent().build(); // 204
    }

    private static ResponseCookie expire(String name, String path) {
        return ResponseCookie.from(name, "")
                .httpOnly(true)
                .secure(true)
                .sameSite("None")
                .path(path)
                .maxAge(Duration.ZERO)
                .build();
    }

    private ResponseCookie createCookie(String name, String value, String path) {
        return ResponseCookie.from(name, value)
                .httpOnly(true)
                .secure(true)
                .sameSite("None")
                .path(path)
                .maxAge(Duration.ofDays(14)) // Refresh Token 수명에 맞춰 조절 가능
                .build();
    }
}