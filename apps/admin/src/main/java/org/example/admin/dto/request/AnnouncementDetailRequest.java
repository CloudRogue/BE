package org.example.admin.dto.request;

import com.fasterxml.jackson.databind.JsonNode;

import java.util.List;

public record AnnouncementDetailRequest(
        String publisher,
        String housingType,
        String supplyType,
        String regionCode,
        String regionName,
        String applyUrl,
        String applyEntryUrl,
        Long rentGtn,
        Long enty,
        Long prtpay,
        Long surlus,
        Long mtRntchrg,
        Eligibility eligibility,
        Submission submission,
        OverviewSummary overviewSummary
) {

    public record Eligibility(List<Answer> answers) {
        public record Answer(
                long additionalOnboardingId,
                String type,
                boolean unknown,
                JsonNode value,
                List<String> options
        ) {}
    }

    public record Submission(
            Dates dates,
            List<Document> applyDocuments,
            List<Document> atDocument
    ) {
        public record Dates(
                String applyStartDate,
                String applyEndDate,
                String documentPublishedAt,
                String finalPublishedAt
        ) {}

        public record Document(
                String name,
                String type
        ) {}
    }

    public record OverviewSummary(
            Overview overview,
            String summary
    ) {
        public record Overview(
                String content,
                String target,
                List<String> regions,
                String applyMethod
        ) {}
    }
}