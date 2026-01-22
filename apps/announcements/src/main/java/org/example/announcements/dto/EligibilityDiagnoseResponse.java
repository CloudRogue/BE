package org.example.announcements.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

import java.time.OffsetDateTime;
import java.util.List;

public record EligibilityDiagnoseResponse(
        @NotNull SupportStatus supportStatus,

        @NotNull
        @JsonFormat(shape = JsonFormat.Shape.STRING)
        OffsetDateTime diagnosedAt,

        @NotNull Integer predictedRank,
        @NotNull Integer predictedBonusPoints,

        @NotEmpty List<TraceItem> trace
) {

    public enum SupportStatus {
        ELIGIBLE,
        INELIGIBLE,
        PENDING
    }

    public record TraceItem(
            @NotBlank String key,
            @NotNull Boolean passed,
            @NotBlank String message
    ) {}
}