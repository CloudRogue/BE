package org.example.auth.exception;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.annotation.Nonnull;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.auth.dto.ApiErrorResponse;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;


import java.io.IOException;

@Slf4j
@Component
@RequiredArgsConstructor
public class AuthEntryPoint implements AuthenticationEntryPoint {

    private final ObjectMapper objectMapper;

    @Override
    public void commence(
            HttpServletRequest request,
            @Nonnull HttpServletResponse response,
            @Nonnull AuthenticationException authException
    ) throws IOException {

        ApiErrorResponse error = resolveError(authException);

        log.warn(
                "[AUTH] AuthenticationEntryPoint - uri={}, code={}, authMsg={}",
                request.getRequestURI(),
                error.code(),
                authException.getMessage(),
                authException
        );

        writeJsonError(response, error);
    }

    private ApiErrorResponse resolveError(AuthenticationException ex) {
        if (ex instanceof OAuth2AuthenticationException) {

            return ApiErrorResponse.of(
                    "AUTH_OAUTH2_ERROR",
                    "소셜 로그인 중 오류가 발생했습니다. 다시 시도해주세요."
            );
        }

        return ApiErrorResponse.of(
                "AUTH_UNAUTHORIZED",
                "인증이 필요합니다."
        );
    }

    private void writeJsonError(HttpServletResponse response, ApiErrorResponse body)
            throws IOException {

        if (response.isCommitted()) {
            return;
        }

        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        response.setContentType("application/json;charset=UTF-8");

        objectMapper.writeValue(response.getWriter(), body);
    }
}