package org.example.mypage.domain;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.example.mypage.domain.enums.UiBlockType;

@Getter
@Entity
@Table(name = "eligibility_profile")
@NoArgsConstructor
public class EligibilityProfile {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank
    @Size(max = 120)
    @Column(nullable = false, length = 120)
    private String title;

    @Size(max = 300)
    @Column(name = "onboarding_description", length = 300)
    private String onboardingDescription;

    @NotBlank
    @Size(max = 300)
    @Column(nullable = false, length = 300)
    private String question;


    @NotBlank
    @Size(max = 300)
    @Column(nullable = false, length = 300)
    private String exampleQuestion;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 32)
    private UiBlockType type;


    public EligibilityProfile(String title, String onboardingDescription, String question, String exampleQuestion, UiBlockType type) {
        this.title = title;
        this.onboardingDescription = onboardingDescription;
        this.question = question;
        this.exampleQuestion = exampleQuestion;
        this.type = type;
    }

    public static EligibilityProfile of(String title, String onboardingDescription, String question, String exampleQuestion, UiBlockType type) {
        return new EligibilityProfile(title, onboardingDescription, question, exampleQuestion, type);
    }
}
