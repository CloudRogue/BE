package org.example.announcements.service;

import lombok.RequiredArgsConstructor;
import org.example.announcements.exception.BusinessException;
import org.example.announcements.exception.ErrorCode;
import org.example.announcements.repository.AnnouncementApplicationRepository;
import org.example.announcements.repository.AnnouncementRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import static org.example.announcements.domain.AnnouncementApplication.create;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class AnnouncementApplyCommandServiceImpl implements AnnouncementApplyCommandService {

    private final AnnouncementRepository announcementRepository;
    private final AnnouncementApplicationRepository announcementApplicationRepository;

    @Override
    @Transactional
    public void apply(String userId, Long announcementId) {
        if (!announcementRepository.existsById(announcementId)) {
            throw new BusinessException(ErrorCode.ANNOUNCEMENT_NOT_FOUND);
        }
        if (announcementApplicationRepository.existsByUserIdAndAnnouncementId(userId, announcementId)) {
            return;
        }
        announcementApplicationRepository.save(create(userId, announcementId));
    }
}
