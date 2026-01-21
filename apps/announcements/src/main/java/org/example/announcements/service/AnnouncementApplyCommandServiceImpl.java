package org.example.announcements.service;

import lombok.RequiredArgsConstructor;
import org.example.announcements.domain.AnnouncementApplication;
import org.example.announcements.exception.BusinessException;
import org.example.announcements.exception.ErrorCode;
import org.example.announcements.repository.AnnouncementApplicationRepository;
import org.example.announcements.repository.AnnouncementRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class AnnouncementApplyCommandServiceImpl implements AnnouncementApplyCommandService {

    private final AnnouncementRepository announcementRepository;
    private final AnnouncementApplicationRepository announcementApplicationRepository;


    @Override
    @Transactional
    public void apply(String userId, Long announcementId) {
        //유저아이디,공고아이디,공고존재 검증
        if (userId == null || userId.isBlank()) {
            throw new BusinessException(ErrorCode.INVALID_REQUEST, "userId가 필요합니다.");
        }
        if (announcementId == null || announcementId < 1) {
            throw new BusinessException(ErrorCode.INVALID_REQUEST, "announcementId 값이 올바르지 않습니다.");
        }
        announcementRepository.findById(announcementId)
                .orElseThrow(() -> new BusinessException(ErrorCode.ANNOUNCEMENT_NOT_FOUND));

        boolean exists = announcementApplicationRepository.existsByUserIdAndAnnouncementId(userId, announcementId);
        if (exists) return;

        announcementApplicationRepository.save(AnnouncementApplication.create(userId, announcementId));
    }
}
