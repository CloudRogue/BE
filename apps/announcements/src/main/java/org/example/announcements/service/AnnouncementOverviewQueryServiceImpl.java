package org.example.announcements.service;

import lombok.RequiredArgsConstructor;
import org.example.announcements.domain.Announcement;
import org.example.announcements.domain.AnnouncementOverview;
import org.example.announcements.dto.AnnouncementOverviewResponse;
import org.example.announcements.exception.BusinessException;
import org.example.announcements.exception.ErrorCode;
import org.example.announcements.repository.AnnouncementOverviewRepository;
import org.example.announcements.repository.AnnouncementRegionRepository;
import org.example.announcements.repository.AnnouncementRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class AnnouncementOverviewQueryServiceImpl implements AnnouncementOverviewQueryService {

    private final AnnouncementRepository announcementRepository;
    private final AnnouncementOverviewRepository overviewRepository;
    private final AnnouncementRegionRepository regionRepository;

    @Override
    public AnnouncementOverviewResponse getOverview(Long announcementId) {

        // 공고가 있나 검증
        Announcement a = announcementRepository.findByIdAndAdminCheckedTrue(announcementId)
                .orElseThrow(() -> new BusinessException(ErrorCode.ANNOUNCEMENT_NOT_FOUND));

        // 아직 없을수있음
        AnnouncementOverview overview = overviewRepository.findByAnnouncementId(a.getId())
                .orElseThrow(() -> new BusinessException(ErrorCode.ANNOUNCEMENT_NOT_FOUND));

        // 지역 목록 조회
        List<String> regions = regionRepository.findAllByAnnouncement_Id(a.getId()).stream()
                .map(r -> r.getRegionName())
                .distinct()
                .toList();

        return AnnouncementOverviewResponse.of(
                a.getId(),
                overview.getContent(),
                overview.getTarget(),
                regions,
                overview.getApplyMethod()
        );
    }
}
