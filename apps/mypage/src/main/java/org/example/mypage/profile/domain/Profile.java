package org.example.mypage.profile.domain;


import jakarta.annotation.Nonnull;
import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.Instant;



@Entity
@Getter
@Table(name = "profile")
@NoArgsConstructor
public class Profile {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank
    @Column(name = "user_id", nullable = false, unique = true, length = 26)
    private String userId;

    @Email
    @NotBlank
    @Column(name = "email", nullable = false, unique = true, length = 320)
    private String email;

    @NotBlank
    @Column(name = "user_name", nullable = false, length = 10)
    private String name;


    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at", nullable = false)
    private Instant updatedAt;

    @Column(name = "deleted_at")
    private Instant deletedAt;


    public Profile(
            @Nonnull String userId,
            @Nonnull String email,
            @Nonnull String name
    ) {
        this.userId = userId;
        this.email = email;
        this.name = name;
    }


}
