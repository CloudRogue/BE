package org.example.announcements.service;

import lombok.RequiredArgsConstructor;
import org.example.announcements.domain.Announcement;
import org.example.announcements.domain.AnnouncementSummary;
import org.example.announcements.dto.AnnouncementSummaryResponse;
import org.example.announcements.exception.BusinessException;
import org.example.announcements.exception.ErrorCode;
import org.example.announcements.repository.AnnouncementRepository;
import org.example.announcements.repository.AnnouncementSummaryRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class AnnouncementSummaryQueryServiceImpl implements AnnouncementSummaryQueryService {

    private final AnnouncementRepository announcementRepository;
    private final AnnouncementSummaryRepository summaryRepository;

    @Override
    public AnnouncementSummaryResponse getSummary(Long announcementId) {
        // 공고 없으면 에러발생
        Announcement a = announcementRepository.findById(announcementId)
                .orElseThrow(() -> new BusinessException(ErrorCode.ANNOUNCEMENT_NOT_FOUND));

        // 요약은 없을 수 있으므로 널가능
        String summary = summaryRepository.findByAnnouncementId(a.getId())
                .map(AnnouncementSummary::getSummary)
                .orElse(null);

        return AnnouncementSummaryResponse.of(a.getId(), summary);
    }
}
