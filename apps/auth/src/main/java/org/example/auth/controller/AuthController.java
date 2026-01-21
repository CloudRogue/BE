package org.example.auth.controller;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import java.time.Duration;


@RestController
@RequestMapping("/api/auth")
public class AuthController {

    @PostMapping("/logout")
    public ResponseEntity<Void> logout(HttpServletResponse response) {

        response.addHeader(HttpHeaders.SET_COOKIE, expireCookie("ACCESS_TOKEN", "/").toString());
        response.addHeader(HttpHeaders.SET_COOKIE, expireCookie("REFRESH_TOKEN", "/api/auth").toString());

        return ResponseEntity.noContent().build(); // 204
    }

    private static ResponseCookie expireCookie(String name, String path) {
        return ResponseCookie.from(name, "")
                .httpOnly(true)
                .secure(false)          // 로그인 때와 동일 (로컬/개발)
                .sameSite("Lax")
                .path(path)
                .maxAge(Duration.ZERO)  // 즉시 만료
                .build();
    }
}
