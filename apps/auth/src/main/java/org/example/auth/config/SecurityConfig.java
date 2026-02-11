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
import org.springframework.http.MediaType;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractAuthenticationFilterConfigurer;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.server.resource.web.BearerTokenResolver;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.LoginUrlAuthenticationEntryPoint;
import org.springframework.security.web.util.matcher.MediaTypeRequestMatcher;
import org.springframework.security.web.util.matcher.NegatedRequestMatcher;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

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
                .cors(cors -> cors.configurationSource(corsConfigurationSource()))
                .sessionManagement(sm -> sm.sessionCreationPolicy(SessionCreationPolicy.IF_REQUIRED))
                .formLogin(form -> form
                        .permitAll()
                        .successHandler(oauth2LoginSuccessHandler)
                )
                .httpBasic(AbstractHttpConfigurer::disable)
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/__login").authenticated()
                        .anyRequest().permitAll()
                )
                .exceptionHandling(ex -> ex
                        .defaultAuthenticationEntryPointFor(
                                new LoginUrlAuthenticationEntryPoint("/login"),
                                new MediaTypeRequestMatcher(MediaType.TEXT_HTML)
                        )
                        .defaultAuthenticationEntryPointFor(
                                authEntryPoint,
                                new NegatedRequestMatcher(new MediaTypeRequestMatcher(MediaType.TEXT_HTML))
                        )
                        .accessDeniedHandler(authAccessDeniedHandler)
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
    public UserDetailsService userDetailsService(PasswordEncoder passwordEncoder) {
        UserDetails u1 = User.withUsername("user")
                .password(passwordEncoder.encode("0000"))
                .roles("USER")
                .build();

        UserDetails u2 = User.withUsername("userS")
                .password(passwordEncoder.encode("0000"))
                .roles("USER")
                .build();

        UserDetails u3 = User.withUsername("userA")
                .password(passwordEncoder.encode("0000"))
                .roles("USER")
                .build();

        return new InMemoryUserDetailsManager(u1, u2, u3);
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }


    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration config = new CorsConfiguration();

        config.setAllowCredentials(true);

        config.setAllowedOriginPatterns(List.of(
                "https://zipchak.deving.xyz",
                "https://zipchakfe.vercel.app",
                "https://*.dsm27o99pz956.amplifyapp.com",
                "http://localhost:3000",
                "http://127.0.0.1:3000",
                "https://localhost:3000",
                "https://127.0.0.1:3000"
        ));

        config.setAllowedMethods(List.of("GET", "POST", "PUT", "PATCH", "DELETE", "OPTIONS"));
        config.setAllowedHeaders(List.of("*"));

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", config);
        return source;
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
