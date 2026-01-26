package org.example.admin.dto.response;

import java.util.List;

public record AdditionalOnboardingBatchCreateResponse(
        List<Item> data
) {
    public record Item(
            long additionalOnboardingId,
            String title,
            String description,
            String question,
            String type,
            boolean required
    ) {}
}
