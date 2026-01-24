package org.example.announcements.listener;

import lombok.RequiredArgsConstructor;
import org.example.announcements.domain.Announcement;
import org.example.announcements.exception.BusinessException;
import org.example.announcements.exception.ErrorCode;
import org.example.announcements.repository.AnnouncementRepository;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.List;

@Component
@RequiredArgsConstructor
public class AnnouncementNotificationListenerImpl implements AnnouncementNotificationListener {

    private final AnnouncementRepository announcementRepository;

    @Override
    public List<Long> findByEndDate(LocalDate targetDate) {
        return announcementRepository.findIdsByEndDateAndAdminCheckedTrue(targetDate);
    }

    @Override
    public List<Long> findByDocumentPublishedAt(LocalDate targetDate) {
        return announcementRepository.findIdsByDocumentPublishedAtAndAdminCheckedTrue(targetDate);
    }

    @Override
    public Announcement getCheckedAnnouncement(Long announcementId) {
        return announcementRepository.findByIdAndAdminCheckedTrue(announcementId)
                .orElseThrow(() -> new BusinessException(
                        ErrorCode.ANNOUNCEMENT_NOT_FOUND,
                        "검수된 공고를 찾을 수 없습니다. announcementId=" + announcementId
                ));
    }
}
