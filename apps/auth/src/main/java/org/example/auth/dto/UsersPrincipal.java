package org.example.auth.dto;

import lombok.Getter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.oauth2.core.user.OAuth2User;

import java.util.Collection;
import java.util.Map;

@Getter
public final class UsersPrincipal implements OAuth2User {
    private final String userId;
    private final String nickname;
    private final Collection<? extends GrantedAuthority> authorities;

    public UsersPrincipal(String userId,
                          String nickname,
                          Collection<? extends GrantedAuthority> authorities) {
        this.userId = userId;
        this.nickname = nickname;
        this.authorities = authorities;
    }

    @Override
    public Map<String, Object> getAttributes() {
        return Map.of();
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return authorities;
    }

    @Override
    public String getName() {
        return userId;
    }
}