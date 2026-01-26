package org.example.mypage.profile.domain;


import com.fasterxml.jackson.databind.JsonNode;
import jakarta.annotation.Nonnull;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;


import java.time.Instant;

/**
 * 사용자별 자격(Eligibility) 온보딩 응답 엔티티.
 *
 * <h2>역할</h2>
 * <ul>
 *   <li>{@link Eligibility}: 어드민이 등록한 UI 블럭(질문 템플릿)</li>
 *   <li>{@link EligibilityAnswer}: 특정 사용자가 해당 템플릿(질문)에 대해 입력한 "응답"</li>
 * </ul>
 *
 * <h2>데이터 무결성(핵심)</h2>
 * <ul>
 *   <li>한 사용자(userId)는 한 템플릿(profile)에 대해 응답이 최대 1개여야 하므로 (user_id, profile_id) UNIQUE를 둔다.</li>
 *   <li>{@code expectedType}은 템플릿의 type을 스냅샷으로 저장한다(= DB CHECK 제약에서 value의 JSON 타입을 강제하기 위해 필요).</li>
 *   <li>{@code value}는 jsonb로 저장하며, type에 따라 허용되는 JSON 타입이 다르다.</li>
 * </ul>
 *
 * <h2>값 규칙</h2>
 * <ul>
 *   <li>BOOLEAN: value는 JSON boolean(true/false)</li>
 *   <li>INPUT / SELECT: value는 JSON string</li>
 *   <li>미응답을 허용하려면 value = null 가능(DDL CHECK에서 value IS NULL 허용)</li>
 * </ul>
 */
@Getter
@Entity
@Table(
        name = "eligibility_answer",
        uniqueConstraints = {
                @UniqueConstraint(
                        name = "uk_answer_user_profile",
                        columnNames = {"user_id", "eligibility_id"}
                )
        },
        indexes = {
                @Index(name = "idx_answer_eligibility_id", columnList = "eligibility_id")
        }
)
@NoArgsConstructor
public class EligibilityAnswer {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank
    @Size(max = 26)
    @Column(name = "user_id", nullable = false, length = 26)
    private String userId;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "eligibility_id", nullable = false, updatable = false)
    private Eligibility eligibility;

    @Setter
    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "value", columnDefinition = "jsonb")
    private JsonNode value;

    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;

    @Column(name = "updated_at", nullable = false)
    private Instant updatedAt;

    public EligibilityAnswer(String userId, Eligibility eligibility, JsonNode value) {
        this.userId = userId;
        this.eligibility = eligibility;
        this.value = value;
    }

    public static EligibilityAnswer create(@Nonnull String userId,
                                           @Nonnull Eligibility profile,
                                           @Nonnull JsonNode value) {

        return new EligibilityAnswer(userId, profile, value);
    }

    public void changeValue(JsonNode value) {
        this.value = value;
    }

    @PrePersist
    void onCreate() {
        Instant now = Instant.now();
        this.createdAt = now;
        this.updatedAt = now;
    }

    @PreUpdate
    void onUpdate() {
        this.updatedAt = Instant.now();
    }
}