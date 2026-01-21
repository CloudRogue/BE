package org.example.announcements.service;

import lombok.RequiredArgsConstructor;
import org.example.announcements.domain.Announcement;
import org.example.announcements.domain.AnnouncementDocument;
import org.example.announcements.domain.AnnouncementDocumentPhase;
import org.example.announcements.dto.ApplicationManagePrepareDetailResponse;
import org.example.announcements.repository.AnnouncementDocumentRepository;
import org.example.announcements.repository.AnnouncementRepository;
import org.example.announcements.repository.AnnouncementSummaryRepository;
import org.example.announcements.util.AnnouncementStatusUtil;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

import static org.example.announcements.util.AnnouncementStatusUtil.*;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ApplicationManagePrepareDetailQueryServiceImpl implements ApplicationManagePrepareDetailQueryService{

    private final AnnouncementRepository announcementRepository;
    private final AnnouncementDocumentRepository announcementDocumentRepository;
    private final AnnouncementSummaryRepository announcementSummaryRepository;

    @Override
    public ApplicationManagePrepareDetailResponse getDetail(Long announcementId) {
        //공고 로드
        Announcement ann = announcementRepository.findById(announcementId)
                .orElseThrow(() -> new IllegalArgumentException("announcement not found: " + announcementId));

        LocalDate today = LocalDate.now();

        //상태 및 디데이 계산
        String currentStatus = calcApplicationManageStatus(
                ann.getEndDate(),
                ann.getDocumentPublishedAt(),
                ann.getFinalPublishedAt(),
                today
        );

        Long dDay = calcApplicationManageDDay(
                currentStatus,
                ann.getEndDate(),
                ann.getDocumentPublishedAt(),
                ann.getFinalPublishedAt(),
                today
        );

        //단계별 제출서류 조회
        List<ApplicationManagePrepareDetailResponse.DocumentItem> applyDocs =
                toDocumentItems(announcementId,
                        announcementDocumentRepository.findAllByAnnouncement_IdAndPhaseOrderByIdAsc(
                                announcementId, AnnouncementDocumentPhase.APPLY
                        ));

        List<ApplicationManagePrepareDetailResponse.DocumentItem> docResultDocs =
                toDocumentItems(announcementId,
                        announcementDocumentRepository.findAllByAnnouncement_IdAndPhaseOrderByIdAsc(
                                announcementId, AnnouncementDocumentPhase.DOC_RESULT
                        ));

        //summary 조회
        String summary = announcementSummaryRepository.findSummaryTextByAnnouncementId(announcementId).orElse(null);


        //응답 조립
        return new ApplicationManagePrepareDetailResponse(
                ann.getId(),
                ann.getTitle(),
                dDay,
                ann.getPublisher(),
                ann.getHousingType(),
                currentStatus,
                ann.getEndDate(),
                ann.getApplyEntryUrl(),
                new ApplicationManagePrepareDetailResponse.ApplySection(
                        ann.getEndDate(),
                        applyDocs
                ),
                new ApplicationManagePrepareDetailResponse.DocResultSection(
                        ann.getDocumentPublishedAt(),
                        docResultDocs
                ),
                new ApplicationManagePrepareDetailResponse.FinalResultSection(
                        ann.getFinalPublishedAt(),
                        summary
                )
        );
    }



    // 응답용으로 변환
    private static List<ApplicationManagePrepareDetailResponse.DocumentItem> toDocumentItems(
            Long announcementId,
            List<AnnouncementDocument> docs
    ) {
        return docs.stream()
                .map(d -> new ApplicationManagePrepareDetailResponse.DocumentItem(
                        announcementId,
                        d.getName(),
                        d.getScope().name()
                ))
                .toList();
    }
}
