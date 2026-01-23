package org.example.mypage.profile.domain;

import com.fasterxml.jackson.databind.JsonNode;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;


@Getter
@Entity
@Table(
        name = "announcement_eligibility",
        uniqueConstraints = {
                @UniqueConstraint(name = "uk_announcement_eligibility", columnNames = {"announcement_id", "eligibility_id"})
        }
)
@NoArgsConstructor
public class AnnouncementEligibility {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "announcement_id", nullable = false)
    private Long announcementId;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "eligibility_id", nullable = false)
    private Eligibility eligibility;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "expected_value", columnDefinition = "jsonb", nullable = false)
    private JsonNode expectedValue;

    public AnnouncementEligibility(Long announcementId, Eligibility eligibility, JsonNode expectedValue) {
        this.announcementId = announcementId;
        this.eligibility = eligibility;
        this.expectedValue = expectedValue;
    }

    public static AnnouncementEligibility of(Long announcementId, Eligibility eligibility, JsonNode expectedValue) {
        return new AnnouncementEligibility(announcementId, eligibility, expectedValue);
    }

    public void changeExpectedValue(JsonNode expectedValue) {
        this.expectedValue = expectedValue;
    }
}
