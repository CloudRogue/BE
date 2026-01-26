package org.example.auth.exception;


import jakarta.annotation.Nonnull;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.auth.dto.ApiErrorResponse;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.stereotype.Component;
import tools.jackson.databind.ObjectMapper;


import java.io.IOException;

@Slf4j
@Component
@RequiredArgsConstructor
public class AuthAccessDeniedHandler implements AccessDeniedHandler {

    private final ObjectMapper objectMapper;

    @Override
    public void handle(
            @Nonnull HttpServletRequest request,
            HttpServletResponse response,
            @Nonnull AccessDeniedException accessDeniedException
    ) throws IOException {

        log.warn("[AUTH] AccessDenied - {}", accessDeniedException.getMessage(), accessDeniedException);

        ApiErrorResponse body = ApiErrorResponse.of(
                "AUTH_FORBIDDEN",
                "해당 리소스에 접근 권한이 없습니다."
        );

        if (response.isCommitted()) {
            return;
        }

        response.reset();
        response.setStatus(HttpServletResponse.SC_FORBIDDEN);
        response.setContentType("application/json;charset=UTF-8");

        objectMapper.writeValue(response.getWriter(), body);
    }

}