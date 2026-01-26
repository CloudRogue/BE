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
public class AdminAnnouncementEnrichPort implements  AdminAnnouncementEnrichUseCase{

    private final AnnouncementRepository announcementRepository;
    private final AnnouncementDocumentRepository announcementDocumentRepository;
    private final AnnouncementRegionRepository announcementRegionRepository;
    private final AnnouncementSummaryRepository announcementSummaryRepository;
    private final AnnouncementOverviewRepository announcementOverviewRepository;

    @Override
    @Transactional
    public void enrich(Long announcementId, AdminAnnouncementEnrichCommand command) {
        //공고 로드
        Announcement ann = announcementRepository.findById(announcementId)
                .orElseThrow(() -> new IllegalArgumentException("announcement not found: " + announcementId));

        //Announcement 없는것만 채우기
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

        //documents
        upsertDocumentsIfAbsent(announcementId, ann, command.submission());

        //summary
        createSummaryIfAbsent(announcementId, command);

        //overview
        createOverviewIfAbsent(announcementId, command);

        //regions
        createRegionsIfAbsent(ann, command);

        //통합저장 완료시 공개상태로 전환
        ann.markAdminChecked();
    }

    //document 메서드(페이즈 별로 이미 존재하면 그대로두기)
    private void upsertDocumentsIfAbsent(Long announcementId, Announcement ann, AdminAnnouncementEnrichCommand.Submission submission) {

        // APPLY 단계 문서
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

        // DOC_RESULT 단계 문서
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

    //summary
    private void createSummaryIfAbsent(Long announcementId, AdminAnnouncementEnrichCommand command) {
        String summary = command.overviewSummary().summary();
        if (summary == null || summary.isBlank()) return;

        if (announcementSummaryRepository.existsByAnnouncementId(announcementId)) {
            return; // 있으면 덮지 않음


        }
        announcementSummaryRepository.save(AnnouncementSummary.create(announcementId, summary.trim()));
    }

    //overview
    private void createOverviewIfAbsent(Long announcementId, AdminAnnouncementEnrichCommand command) {
        var ov = command.overviewSummary().overview();
        if (ov == null) return;

        if (isBlank(ov.content()) || isBlank(ov.target()) || isBlank(ov.applyMethod())) return;

        if (announcementOverviewRepository.existsByAnnouncementId(announcementId)) {
            return;

        }

        announcementOverviewRepository.save(
                AnnouncementOverview.create(
                        announcementId,
                        ov.content().trim(),
                        ov.target().trim(),
                        ov.applyMethod().trim()
                )
        );
    }


    private void createRegionsIfAbsent(Announcement ann, AdminAnnouncementEnrichCommand command) {
        AdminAnnouncementEnrichCommand.OverviewSummary overviewSummary = command.overviewSummary();
        AdminAnnouncementEnrichCommand.Overview overview = overviewSummary == null ? null : overviewSummary.overview();

        if (overview == null || overview.regions() == null || overview.regions().isEmpty()) {
            return;
        }

        Set<String> regionNames = new LinkedHashSet<>();
        for (String r : overview.regions()) {
            if (r == null) continue;
            String trimmed = r.trim();
            if (!trimmed.isBlank()) {
                regionNames.add(trimmed);
            }
        }
        if (regionNames.isEmpty()) {
            return;
        }

        List<String> existingList = announcementRegionRepository.findRegionNamesByAnnouncementId(ann.getId());
        Set<String> existing = new java.util.HashSet<>(existingList);

        List<AnnouncementRegion> toSave = regionNames.stream()
                .filter(regionName -> !existing.contains(regionName))
                .map(regionName -> AnnouncementRegion.create(ann, regionName))
                .toList();

        if (toSave.isEmpty()) {
            return;
        }

        announcementRegionRepository.saveAll(toSave);
    }

    private static boolean isBlank(String s) {
        return s == null || s.isBlank();
    }
}