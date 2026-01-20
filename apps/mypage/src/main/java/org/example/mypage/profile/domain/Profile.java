package org.example.mypage.profile.domain;


import jakarta.annotation.Nonnull;
import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.example.mypage.profile.dto.request.ProfilePatchRequest;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.Instant;


/**
 * 사용자 프로필 엔티티입니다.
 *
 * <h2>역할</h2>
 * <ul>
 *   <li>온보딩/맞춤 공고 필터링에 필요한 사용자 기본 정보를 저장합니다.</li>
 *   <li>인증 계정(로그인/권한)과 분리된 "프로필 도메인" 데이터이며, 추천/자격판단의 입력값으로 사용됩니다.</li>
 * </ul>
 *
 * <h2>주요 필드 의미</h2>
 * <ul>
 *   <li>{@link #userId}: 사용자 식별자(예: ULID). 인증 토큰의 subject와 매핑되는 값</li>
 *   <li>{@link #email}: 사용자 이메일(형식 검증은 {@code @Email}, 값 존재는 {@code @NotBlank})</li>
 *   <li>{@link #name}: 사용자 이름</li>
 *   <li>{@link #gender}: 성별</li>
 *   <li>{@link #regionSigungu}: 거주 지역(시/군/구). 맞춤 공고의 지역 조건 판단에 활용</li>
 *   <li>{@link #householdSize}: 가구원 수</li>
 *   <li>{@link #birthDate}: 생년월일. 연령 조건 판단에 활용</li>
 *   <li>{@link #isMarried}: 혼인 여부</li>
 *   <li>{@link #isHomeless}: 무주택 여부</li>
 *   <li>{@link #householdRole}: 가구 내 역할(예: 세대주/세대원 등). 공고 자격 판단에 활용</li>
 * </ul>
 *
 * <h2>시간 컬럼</h2>
 * <ul>
 *   <li>{@link #createdAt}: 생성 시각(UTC 기준 {@link java.time.Instant})</li>
 *   <li>{@link #updatedAt}: 수정 시각(UTC 기준 {@link java.time.Instant})</li>
 *   <li>{@link #deletedAt}: 삭제 시각(soft delete)</li>
 * </ul>
 *
 * <h2>변경 규칙</h2>
 * <ul>
 *   <li>{@link #patch(ProfilePatchRequest)}는 null이 아닌 값만 반영하는 부분 수정용 메서드입니다.</li>
 *   <li>boolean 필드는 PATCH에서 “수정/미수정” 구분을 위해 요청 DTO에서 {@link Boolean} (boxed)로 받는 것을 전제로 합니다.</li>
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
