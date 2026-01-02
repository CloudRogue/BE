package org.example.auth.domain;


import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(
        name = "users"
)
@Getter
@NoArgsConstructor
public class Users {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "user_id", nullable = false, length = 26, unique = true)
    private String userId;

    @Column(name = "name", length = 50)
    private String name;

    @Column(name = "email")
    private String email;

    @Column(name = "provider", nullable = false, length = 20)
    private OAuthProvider provider;

    @Column(name = "provider_user_id", nullable = false, length = 100)
    private String providerUserId;

    @Enumerated(EnumType.STRING)
    @Column(name = "role", nullable = false, length = 20)
    private UserRole role;

    public Users(String userId, String name, String email,
                 OAuthProvider provider, String providerUserId) {
        this.userId = userId;
        this.name = name;
        this.email = email;
        this.provider = provider;
        this.providerUserId = providerUserId;
        this.role = UserRole.MEMBER;
    }
}
