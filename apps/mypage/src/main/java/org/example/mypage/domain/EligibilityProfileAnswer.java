package org.example.mypage.domain;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.BooleanNode;
import com.fasterxml.jackson.databind.node.TextNode;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.example.mypage.domain.enums.UiBlockType;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;
import java.time.Instant;

/**
 * 사용자별 자격(Eligibility) 온보딩 응답 엔티티.
 *
 * <h2>역할</h2>
 * <ul>
 *   <li>{@link EligibilityProfile}: 어드민이 등록한 UI 블럭(질문 템플릿)</li>
 *   <li>{@link EligibilityProfileAnswer}: 특정 사용자가 해당 템플릿(질문)에 대해 입력한 "응답"</li>
 * </ul>
 *
 * <h2>데이터 무결성(핵심)</h2>
 * <ul>
 *   <li>한 사용자(userId)는 한 템플릿(profile)에 대해 응답이 최대 1개여야 하므로 (user_id, profile_id) UNIQUE를 둔다.</li>
 *   <li>{@code expectedType}은 템플릿의 type을 스냅샷으로 저장한다(= DB CHECK 제약에서 value의 JSON 타입을 강제하기 위해 필요).</li>
 *   <li>{@code value}는 jsonb로 저장하며, type에 따라 허용되는 JSON 타입이 다르다.</li>
 * </ul>
 *
 * exampleText 는 ai 성능을 위한 주석입니다
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
        name = "eligibility_profile_answer",
        uniqueConstraints = {
                @UniqueConstraint(
                        name = "uk_answer_user_profile",
                        columnNames = {"user_id", "profile_id"}
                )
        },
        indexes = {
                @Index(name = "idx_answer_user_id", columnList = "user_id"),
                @Index(name = "idx_answer_profile_id", columnList = "profile_id")
        }
)
@NoArgsConstructor
public class EligibilityProfileAnswer {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank
    @Size(max = 26)
    @Column(name = "user_id", nullable = false, length = 26)
    private String userId;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "profile_id", nullable = false, updatable = false)
    private EligibilityProfile profile;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(name = "expected_type", nullable = false, length = 32)
    private UiBlockType expectedType;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "value", columnDefinition = "jsonb")
    private JsonNode value;

    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;

    @Column(name = "updated_at", nullable = false)
    private Instant updatedAt;

    @Column(name = "example_text")
    private String exampleText;

    public EligibilityProfileAnswer(String userId, EligibilityProfile profile, UiBlockType expectedType, JsonNode value, String exampleText) {
        this.userId = userId;
        this.profile = profile;
        this.expectedType = expectedType;
        this.value = value;
        this.exampleText = exampleText;
    }

    public static EligibilityProfileAnswer empty(String userId, EligibilityProfile profile, String exampleText) {
        return new EligibilityProfileAnswer(userId, profile, profile.getType(), null, exampleText);
    }

    public static EligibilityProfileAnswer ofBoolean(String userId, EligibilityProfile profile, boolean v, String exampleText) {
        return new EligibilityProfileAnswer(userId, profile, profile.getType(), BooleanNode.valueOf(v), exampleText);
    }

    public static EligibilityProfileAnswer ofInput(String userId, EligibilityProfile profile, String v, String exampleText) {
        return new EligibilityProfileAnswer(userId, profile, profile.getType(), TextNode.valueOf(v), exampleText);
    }

    public static EligibilityProfileAnswer ofSelect(String userId, EligibilityProfile profile, String selectedValue, String exampleText) {
        return new EligibilityProfileAnswer(userId, profile, profile.getType(), TextNode.valueOf(selectedValue), exampleText);
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