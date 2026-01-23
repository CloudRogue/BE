package org.example.mypage.profile.dto.response;

import org.example.mypage.profile.domain.enums.UiBlockType;

import java.util.List;

public record EligibilityCatalogResponse(
        List<Item> data
) {
    public record Item(
            Long additionalOnboardingId,
            String title,
            String description,
            String question,
            UiBlockType type,
            boolean required
    ) {}
}
