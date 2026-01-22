package org.example.auth.config;

import jakarta.servlet.http.Cookie;
import lombok.RequiredArgsConstructor;
import org.example.auth.dto.UsersPrincipal;
import org.example.auth.exception.AuthAccessDeniedHandler;
import org.example.auth.exception.AuthEntryPoint;
import org.example.auth.service.CustomOAuth2UserService;
import org.example.auth.service.Oauth2LoginSuccessHandler;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.convert.converter.Converter;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.server.resource.web.BearerTokenResolver;
import org.springframework.security.web.SecurityFilterChain;

import java.util.List;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {
    private static final String OAUTH2_FAILURE_REDIRECT_URL = "https://localhost:3000/fail";
    
    private final AuthEntryPoint authEntryPoint;
    private final AuthAccessDeniedHandler authAccessDeniedHandler;

    private final CustomOAuth2UserService customOAuth2UserService;
    private final Oauth2LoginSuccessHandler oauth2LoginSuccessHandler;
    private final JwtDecoder jwtDecoder;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf(AbstractHttpConfigurer::disable)
                .sessionManagement(sm -> sm.sessionCreationPolicy(SessionCreationPolicy.IF_REQUIRED))
                .formLogin(AbstractHttpConfigurer::disable)
                .httpBasic(AbstractHttpConfigurer::disable)

                // ⚠️ 토이 프로젝트 편의 설정: 운영 환경에서는 사용 금지
                // - 모든 엔드포인트를 인증 없이 허용합니다.
                // - 운영에서는 최소한 /api/**, /ws 등은 인증/인가 정책을 반드시 적용하세요.
                .authorizeHttpRequests(auth -> auth
                        .anyRequest().permitAll()
                )
                .exceptionHandling(ex -> ex
                        .authenticationEntryPoint(authEntryPoint)       // 401: 인증 없음/실패
                        .accessDeniedHandler(authAccessDeniedHandler)   // 403: 권한 부족
                )
                .oauth2Login(oauth2 -> oauth2
                        .userInfoEndpoint(userInfo -> userInfo
                                .userService(customOAuth2UserService)
                        )
                        .successHandler(oauth2LoginSuccessHandler)
                        .failureHandler(oauth2FailureHandler())
                )
                .oauth2ResourceServer(resource -> resource
                        .authenticationEntryPoint(authEntryPoint)
                        .accessDeniedHandler(authAccessDeniedHandler)
                        .bearerTokenResolver(bearerTokenResolver())
                        .jwt(jwt -> jwt
                                .decoder(jwtDecoder)
                                .jwtAuthenticationConverter(jwtToUsersPrincipal())
                        )
                );

        return http.build();
    }


   @Bean
    public AuthenticationFailureHandler oauth2FailureHandler() {
        return (request, response, exception) -> {
            // 일단 실패는 다보냈습니다
            response.sendRedirect(OAUTH2_FAILURE_REDIRECT_URL);
        };
    }
    
    @Bean
    public BearerTokenResolver bearerTokenResolver() {
        return request -> {
            Cookie[] cookies = request.getCookies();
            if (cookies == null) {
                return null;
            }
            for (Cookie cookie : cookies) {
                if ("ACCESS_TOKEN".equals(cookie.getName())) {
                    return cookie.getValue();
                }
            }
            return null;
        };
    }

    @Bean
    public Converter<Jwt, ? extends AbstractAuthenticationToken> jwtToUsersPrincipal() {
        return (Jwt jwt) -> {
            List<GrantedAuthority> authorities =
                    List.of(new SimpleGrantedAuthority("ROLE_USER"));

            String userId = jwt.getSubject();
            Object nicknameClaim = jwt.getClaims().get("nickname");
            String nickname = nicknameClaim == null ? "" : nicknameClaim.toString();

            UsersPrincipal principal = new UsersPrincipal(
                    userId,
                    nickname,
                    authorities,
                    false
            );

            return new UsernamePasswordAuthenticationToken(principal, jwt, authorities);
        };
    }
}
