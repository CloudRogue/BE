package org.example.auth.controller;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.Duration;


@RestController
@RequestMapping("/api/auth")
public class AuthController {

    @PostMapping("/logout")
    public ResponseEntity<Void> logout(HttpServletResponse response) {

        response.addHeader(HttpHeaders.SET_COOKIE, expire("ACCESS_TOKEN", "/").toString());
        response.addHeader(HttpHeaders.SET_COOKIE, expire("REFRESH_TOKEN", "/api/auth").toString());

        return ResponseEntity.noContent().build(); // 204
    }


    @GetMapping("/__login")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void loginTrigger() {
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
}