package org.example.core.mypage.domain;


import jakarta.annotation.Nonnull;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.example.core.mypage.domain.enums.Gender;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.Instant;
import java.time.LocalDate;

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

    @NotBlank
    @Column(name = "user_name", nullable = false, length = 10)
    private String name;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(name = "gender", nullable = false, length = 6)
    private Gender gender;

    @NotBlank
    @Column(name = "region_sigungu", nullable = false, length = 100)
    private String regionSigungu;

    @Column(name = "income_decile", nullable = false)
    private int incomeDecile;

    @Column(name = "household_size", nullable = false)
    private int householdSize;

    @NotNull
    @Column(name = "birth_date", nullable = false)
    private LocalDate birthDate;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at", nullable = false)
    private Instant updatedAt;

    @Column(name = "deleted_at")
    private Instant deletedAt;

    public Profile(@Nonnull String userId, @Nonnull String name, @Nonnull Gender gender, @Nonnull String regionSigungu, int incomeDecile, int householdSize, @Nonnull LocalDate birthDate) {
        this.userId = userId;
        this.name = name;
        this.gender = gender;
        this.regionSigungu = regionSigungu;
        this.incomeDecile = incomeDecile;
        this.householdSize = householdSize;
        this.birthDate = birthDate;
    }
}
