package org.example.auth.service;

import jakarta.annotation.Nonnull;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.example.auth.dto.UsersPrincipal;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.Objects;

@Component
@RequiredArgsConstructor
public class Oauth2LoginSuccessHandler implements AuthenticationSuccessHandler {
    private final JwtService jwtService;

    @Value("${auth.redirect.success-url}")
    private String successRedirectUrl;

    @Value("${auth.redirect.success-new-url}")
    private String new_successRedirectUrl;

    @Override
    public void onAuthenticationSuccess(@Nonnull HttpServletRequest request, @Nonnull HttpServletResponse response, @Nonnull Authentication authentication) throws IOException, ServletException {
        UsersPrincipal principal = (UsersPrincipal) authentication.getPrincipal();
        Objects.requireNonNull(principal, "principal must not be null");

        String accessToken  = jwtService.createAccessToken(principal);
        String refreshToken = jwtService.createRefreshToken(principal);

        // local test시 false, deploy시 true로 변경 필요
        ResponseCookie accessCookie = ResponseCookie.from("ACCESS_TOKEN", accessToken)
                .httpOnly(true)
                .secure(false)
                .path("/")
                .maxAge(60 * 60)
                .sameSite("Lax")
                .build();

        ResponseCookie refreshCookie = ResponseCookie.from("REFRESH_TOKEN", refreshToken)
                .httpOnly(true)
                .secure(false)
                .path("/api/auth")
                .maxAge(60L * 60 * 24 * 14)
                .sameSite("Lax")
                .build();

        response.addHeader(HttpHeaders.SET_COOKIE, accessCookie.toString());
        response.addHeader(HttpHeaders.SET_COOKIE, refreshCookie.toString());

        String redirectUrl = principal.isNew()
                ? new_successRedirectUrl
                : successRedirectUrl;

        response.sendRedirect(redirectUrl);
    }
}