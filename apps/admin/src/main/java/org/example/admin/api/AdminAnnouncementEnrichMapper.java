package org.example.admin.api;

import org.example.admin.dto.request.AnnouncementDetailRequest;
import org.example.announcements.port.AdminAnnouncementEnrichCommand;

import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.List;

public final class AdminAnnouncementEnrichMapper {

    private AdminAnnouncementEnrichMapper() {}


    public static AdminAnnouncementEnrichCommand toCommand(AnnouncementDetailRequest req) {
        if (req == null) throw new IllegalArgumentException("request must not be null");

        return new AdminAnnouncementEnrichCommand(
                req.publisher(),
                req.housingType(),
                req.supplyType(),
                req.regionCode(),
                req.regionName(),

                req.applyUrl(),
                req.applyEntryUrl(),
                req.rentGtn(),
                req.enty(),
                req.prtpay(),
                req.surlus(),
                req.mtRntchrg(),

                toSubmission(req.submission()),
                toOverviewSummary(req.overviewSummary())
        );
    }

    private static AdminAnnouncementEnrichCommand.Submission toSubmission(AnnouncementDetailRequest.Submission s) {
        if (s == null) return null;

        return new AdminAnnouncementEnrichCommand.Submission(
                toDates(s.dates()),
                toDocInputs(s.applyDocuments()),
                toDocInputs(s.atDocument())
        );
    }

    private static AdminAnnouncementEnrichCommand.Dates toDates(AnnouncementDetailRequest.Submission.Dates d) {
        if (d == null) return null;

        return new AdminAnnouncementEnrichCommand.Dates(
                parseLocalDateOrNull(d.applyStartDate(), "applyStartDate"),
                parseLocalDateOrNull(d.applyEndDate(), "applyEndDate"),
                parseLocalDateOrNull(d.documentPublishedAt(), "documentPublishedAt"),
                parseLocalDateOrNull(d.finalPublishedAt(), "finalPublishedAt")
        );
    }

    private static List<AdminAnnouncementEnrichCommand.DocumentInput> toDocInputs(
            List<AnnouncementDetailRequest.Submission.Document> docs
    ) {
        if (docs == null) return null;

        return docs.stream()
                .map(doc -> new AdminAnnouncementEnrichCommand.DocumentInput(
                        doc.name(),
                        doc.type()
                ))
                .toList();
    }

    private static AdminAnnouncementEnrichCommand.OverviewSummary toOverviewSummary(
            AnnouncementDetailRequest.OverviewSummary os
    ) {
        if (os == null) return null;

        return new AdminAnnouncementEnrichCommand.OverviewSummary(
                toOverview(os.overview()),
                os.summary()
        );
    }

    private static AdminAnnouncementEnrichCommand.Overview toOverview(
            AnnouncementDetailRequest.OverviewSummary.Overview o
    ) {
        if (o == null) return null;

        return new AdminAnnouncementEnrichCommand.Overview(
                o.content(),
                o.target(),
                o.regions(),
                o.applyMethod()
        );
    }

    private static LocalDate parseLocalDateOrNull(String s, String fieldName) {
        if (s == null || s.isBlank()) return null;
        try {
            return LocalDate.parse(s);
        } catch (DateTimeParseException e) {
            throw new IllegalArgumentException("invalid date for " + fieldName + ": " + s, e);
        }
    }
}
