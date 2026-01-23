package org.example.admin.api;

import java.util.List;

public record EligibilityCatalogResponse(
        List<Item> data
) {
    public record Item(
            Long additionalOnboardingId,
            String title,
            String description,
            String question,
            String type,
            boolean required
    ) {}
}