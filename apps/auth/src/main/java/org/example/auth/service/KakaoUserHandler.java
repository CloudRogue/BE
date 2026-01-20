package org.example.auth.service;

import com.github.f4b6a3.ulid.UlidCreator;
import lombok.RequiredArgsConstructor;
import org.example.auth.domain.OAuthProvider;
import org.example.auth.domain.UserRole;
import org.example.auth.domain.Users;
import org.example.auth.dto.UsersPrincipal;
import org.example.auth.repository.UserRepository;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Component;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Component
@RequiredArgsConstructor
public class KakaoUserHandler implements ProviderUserHandler {

    private final UserRepository usersRepository;

    @Override
    public boolean supports(String registrationId) {
        return OAuthProvider.KAKAO.name().equalsIgnoreCase(registrationId);
    }

    @Override
    @SuppressWarnings("unchecked")
    public OAuth2User handle(OAuth2UserRequest userRequest, Map<String, Object> attributes) {

        Object rawId = attributes.get("id");
        if (!(rawId instanceof Number)) {
            throw new OAuth2AuthenticationException("카카오 응답 형식이 변경되었습니다.(id)");
        }
        Long kakaoId = ((Number) rawId).longValue();

        String providerUserId = String.valueOf(kakaoId);

        Map<String, Object> kakaoAccount = (Map<String, Object>) attributes.getOrDefault("kakao_account", Map.of());
        Map<String, Object> profile      = (Map<String, Object>) kakaoAccount.getOrDefault("profile", Map.of());

        String email = (String) kakaoAccount.get("email");
        String name = (String) profile.get("nickname");

        if (name == null) {
            throw new OAuth2AuthenticationException("카카오 프로필 닉네임이 없습니다.");
        }

        Optional<Users> existing = usersRepository
                .findByProviderAndProviderUserId(OAuthProvider.KAKAO, providerUserId);

        boolean isNew = existing.isEmpty();

        Users user = existing.orElseGet(() -> {
            String newUserId = UlidCreator.getUlid().toString();
            Users newUser = new Users(
                    newUserId,
                    name,
                    email,
                    OAuthProvider.KAKAO,
                    providerUserId
            );
            return usersRepository.save(newUser);
        });

        Collection<GrantedAuthority> authorities =
                List.of(new SimpleGrantedAuthority(UserRole.MEMBER.authority()));

        return new UsersPrincipal(
                user.getUserId(),
                user.getName(),
                authorities,
                isNew
        );
    }
}