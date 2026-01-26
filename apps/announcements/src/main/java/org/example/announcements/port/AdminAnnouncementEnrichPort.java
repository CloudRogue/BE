package org.example.announcements.port;


import lombok.RequiredArgsConstructor;
import org.example.announcements.domain.*;
import org.example.announcements.repository.*;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class AdminAnnouncementEnrichPort implements AdminAnnouncementEnrichUseCase {

    private final AnnouncementRepository announcementRepository;
    private final AnnouncementDocumentRepository announcementDocumentRepository;
    private final AnnouncementRegionRepository announcementRegionRepository;
    private final AnnouncementSummaryRepository announcementSummaryRepository;
    private final AnnouncementOverviewRepository announcementOverviewRepository;

    @Override
    @Transactional
    public void enrich(Long announcementId, AdminAnnouncementEnrichCommand command) {
        Announcement ann = announcementRepository.findById(announcementId)
                .orElseThrow(() -> new IllegalArgumentException("announcement not found: " + announcementId));

        var dates = command.submission().dates();

        ann.enrichIfAbsent(
                command.publisher(),
                command.housingType(),
                command.supplyType(),
                command.regionCode(),
                command.regionName(),
                command.applyUrl(),
                command.applyEntryUrl(),
                dates.applyStartDate(),
                dates.applyEndDate(),
                dates.documentPublishedAt(),
                dates.finalPublishedAt(),
                command.rentGtn(),
                command.enty(),
                command.prtpay(),
                command.surlus(),
                command.mtRntchrg()
        );
        upsertDocumentsIfAbsent(announcementId, ann, command.submission());
        createSummaryBestEffort(announcementId, command);
        createOverviewBestEffort(announcementId, command);
        createRegionsBestEffort(ann, command);
        ann.markAdminChecked();
    }

    private void upsertDocumentsIfAbsent(Long announcementId, Announcement ann, AdminAnnouncementEnrichCommand.Submission submission) {

        if (!announcementDocumentRepository.existsByAnnouncement_IdAndPhase(announcementId, AnnouncementDocumentPhase.APPLY)) {
            List<AnnouncementDocument> applyDocs = submission.applyDocuments().stream()
                    .filter(d -> d != null && d.name() != null && !d.name().isBlank())
                    .map(d -> AnnouncementDocument.create(
                            ann,
                            AnnouncementDocumentPhase.APPLY,
                            AnnouncementDocumentScope.valueOf(d.scope()),
                            d.name().trim()
                    ))
                    .toList();

            if (!applyDocs.isEmpty()) {
                announcementDocumentRepository.saveAll(applyDocs);
            }
        }

        if (!announcementDocumentRepository.existsByAnnouncement_IdAndPhase(announcementId, AnnouncementDocumentPhase.DOC_RESULT)) {
            List<AnnouncementDocument> docResultDocs = submission.atDocument().stream()
                    .filter(d -> d != null && d.name() != null && !d.name().isBlank())
                    .map(d -> AnnouncementDocument.create(
                            ann,
                            AnnouncementDocumentPhase.DOC_RESULT,
                            AnnouncementDocumentScope.valueOf(d.scope()),
                            d.name().trim()
                    ))
                    .toList();

            if (!docResultDocs.isEmpty()) {
                announcementDocumentRepository.saveAll(docResultDocs);
            }
        }
    }

    private void createSummaryBestEffort(Long announcementId, AdminAnnouncementEnrichCommand command) {
        String summary = command.overviewSummary().summary();
        if (summary == null || summary.isBlank()) return;

        try {

            announcementSummaryRepository.save(AnnouncementSummary.create(announcementId, summary.trim()));
        } catch (DataIntegrityViolationException e) {
            if (isUniqueConstraintViolation(e, "uq_announcement_summary_announcement_id")) return;
            throw e;
        }
    }

    private void createOverviewBestEffort(Long announcementId, AdminAnnouncementEnrichCommand command) {
        var ov = command.overviewSummary().overview();
        if (ov == null) return;

        if (isBlank(ov.content()) || isBlank(ov.target()) || isBlank(ov.applyMethod())) return;

        try {
            announcementOverviewRepository.save(
                    AnnouncementOverview.create(
                            announcementId,
                            ov.content().trim(),
                            ov.target().trim(),
                            ov.applyMethod().trim()
                    )
            );
        } catch (DataIntegrityViolationException e) {
            if (isUniqueConstraintViolation(e, "uq_announcement_overview_announcement_id")) return;
            throw e;
        }
    }

    private void createRegionsBestEffort(Announcement ann, AdminAnnouncementEnrichCommand command) {
        var ov = command.overviewSummary().overview();
        if (ov == null || ov.regions() == null || ov.regions().isEmpty()) return;

        Set<String> regionNames = new LinkedHashSet<>();
        for (String r : ov.regions()) {
            if (r == null) continue;
            String trimmed = r.trim();
            if (!trimmed.isBlank()) regionNames.add(trimmed);
        }
        if (regionNames.isEmpty()) return;

        for (String regionName : regionNames) {
            try {
                announcementRegionRepository.save(AnnouncementRegion.create(ann, regionName));
            } catch (DataIntegrityViolationException e) {
                if (isUniqueConstraintViolation(e, "uq_announcement_region")) continue;
                throw e;
            }
        }
    }

    private static boolean isBlank(String s) {
        return s == null || s.isBlank();
    }

    private static boolean isUniqueConstraintViolation(Throwable t, String constraintName) {
        Throwable cur = t;
        while (cur != null) {
            String msg = cur.getMessage();
            if (msg != null && msg.contains(constraintName)) return true;
            cur = cur.getCause();
        }
        return false;
    }
}