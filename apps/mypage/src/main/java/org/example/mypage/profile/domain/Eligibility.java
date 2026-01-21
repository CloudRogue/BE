package org.example.mypage.profile.domain;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.example.mypage.profile.domain.enums.UiBlockType;

@Getter
@Entity
@Table(name = "eligibility")
@NoArgsConstructor
public class Eligibility {

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
    @Column(name = "question", nullable = false, length = 300)
    private String question;


    @NotBlank
    @Size(max = 300)
    @Column(name = "example_question", nullable = false, length = 300)
    private String exampleQuestion;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 32)
    private UiBlockType type;

    @Column(name = "required_onboarding", nullable = false)
    private boolean requiredOnboarding;

    public Eligibility(String title, String onboardingDescription, String question, String exampleQuestion, UiBlockType type) {
        this.title = title;
        this.onboardingDescription = onboardingDescription;
        this.question = question;
        this.exampleQuestion = exampleQuestion;
        this.type = type;
        this.requiredOnboarding = false;
    }

    public static Eligibility of(String title, String onboardingDescription, String question, String exampleQuestion, UiBlockType type) {
        return new Eligibility(title, onboardingDescription, question, exampleQuestion, type);
    }
}
