package org.example.mypage.profile.domain;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Entity
@Table(
        name = "eligibility_option",
        uniqueConstraints = {
                @UniqueConstraint(
                        name = "uk_eligibility_option_eligibility_display_order",
                        columnNames = {"eligibility_id", "display_order"}
                )
        }
)
@NoArgsConstructor
public class EligibilityOption {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "eligibility_id", nullable = false, updatable = false)
    private Eligibility eligibility;

    @Column(nullable = false, length = 120)
    private String label;

    @Column(name = "display_order", nullable = false)
    private int displayOrder;

    private EligibilityOption(Eligibility eligibility, String label, int displayOrder) {
        this.eligibility = eligibility;
        this.label = label;
        this.displayOrder = displayOrder;
    }

    public static EligibilityOption of(Eligibility eligibility, String label, int displayOrder) {
        return new EligibilityOption(eligibility, label, displayOrder);
    }
}
