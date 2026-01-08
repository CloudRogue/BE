package org.example.mypage.domain;


import jakarta.annotation.Nonnull;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.example.mypage.domain.enums.Gender;
import org.example.mypage.domain.enums.HouseholdRole;
import org.example.mypage.dto.request.ProfilePatchRequest;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.Instant;
import java.time.LocalDate;


/**
 * 사용자 프로필 엔티티입니다.
 *
 * <h2>역할</h2>
 * <ul>
 *   <li>온보딩/맞춤 공고 필터링에 필요한 사용자 기본 정보를 저장합니다.</li>
 *   <li>인증 계정(로그인 정보)과는 별도로 관리되는 "프로필 도메인" 데이터입니다.</li>
 * </ul>
 *
 * <h2>데이터 특성</h2>
 * <ul>
 *   <li>사용자 1명당 1개의 프로필 row를 가집니다. ({@link #userId} 유니크)</li>
 *   <li>삭제는 {@link #deletedAt}을 사용하는 논리 삭제(soft delete) 형태입니다.</li>
 * </ul>
 *
 * <h2>주요 필드 의미</h2>
 * <ul>
 *   <li>{@link #regionSigungu}: 거주 지역(시/군/구). 맞춤 공고의 지역 조건 판단에 활용됩니다.</li>
 *   <li>{@link #incomeDecile}: 소득 분위(1~10 가정). 정책/공고 자격 조건 판단에 활용됩니다.</li>
 *   <li>{@link #householdSize}: 가구원 수.</li>
 *   <li>{@link #birthDate}: 생년월일. 연령 조건 판단에 활용됩니다.</li>
 *   <li>{@link #householdRole}: 가구 내 역할(예: 세대주/세대원 등). 공고 자격 판단에 활용됩니다.</li>
 * </ul>
 *
 * <h2>시간 컬럼</h2>
 * <ul>
 *   <li>{@link #createdAt}: 생성 시각(UTC 기준 {@link Instant})</li>
 *   <li>{@link #updatedAt}: 수정 시각(UTC 기준 {@link Instant})</li>
 * </ul>
 */
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

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(name = "household_role", nullable = false, length = 20)
    private HouseholdRole householdRole;

    public Profile(@Nonnull String userId, @Nonnull String name, @Nonnull Gender gender, @Nonnull String regionSigungu, int incomeDecile, int householdSize, @Nonnull LocalDate birthDate, @Nonnull HouseholdRole householdRole) {
        this.userId = userId;
        this.name = name;
        this.gender = gender;
        this.regionSigungu = regionSigungu;
        this.incomeDecile = incomeDecile;
        this.householdSize = householdSize;
        this.birthDate = birthDate;
        this.householdRole = householdRole;
    }

    public void patch(ProfilePatchRequest req) {
        if (req.name() != null) this.name = req.name();
        if (req.gender() != null) this.gender = req.gender();
        if (req.birthDate() != null) this.birthDate = req.birthDate();
        if (req.regionSigungu() != null) this.regionSigungu = req.regionSigungu();
        if (req.householdSize() != null) this.householdSize = req.householdSize();
        if (req.incomeDecile() != null) this.incomeDecile = req.incomeDecile();
        if (req.householdRole() != null) this.householdRole = req.householdRole();
    }
}
