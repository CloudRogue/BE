package org.example.mypage.profile.dto.response;


import java.util.List;

public record OnboardingQuestionResponse(
        List<Item> data
) {
    public record Item(
            Long requiredOnboardingId,
            String title,
            String description,
            String question,
            String type,
            List<String> options
    ) {}
}
