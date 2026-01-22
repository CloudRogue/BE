package org.example.mypage.profile.dto.request;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;

public record EligibilityAnswersRequest(
        @NotNull @Valid OnboardingRequest eligibility
) {}
