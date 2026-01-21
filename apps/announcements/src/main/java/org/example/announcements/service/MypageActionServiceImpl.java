package org.example.announcements.service;

import lombok.RequiredArgsConstructor;
import org.example.announcements.dto.internal.mypage.MypageOutboundRequest;
import org.example.announcements.dto.internal.mypage.MypageScrapRequest;
import org.example.announcements.exception.BusinessException;
import org.example.announcements.exception.ErrorCode;
import org.example.announcements.repository.AnnouncementRepository;
import org.example.announcements.service.internal.mypage.MypageClient;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class MypageActionServiceImpl implements MypageActionService {

    private final AnnouncementRepository announcementRepository;
    private final MypageClient mypageClient;



    @Override
    @Transactional
    public void recordOutbound(String userId, Long announcementId) {
        assertAnnouncementExists(announcementId);
        mypageClient.postOutbound(new MypageOutboundRequest(userId, announcementId));
    }


    @Override
    @Transactional
    public void addScrap(String userId, Long announcementId) {
        assertAnnouncementExists(announcementId);
        mypageClient.postScrap(new MypageScrapRequest(userId, announcementId));
    }

    @Override
    @Transactional
    public void removeScrap(String userId, Long announcementId) {
        assertAnnouncementExists(announcementId);
        mypageClient.deleteScrap(new MypageScrapRequest(userId, announcementId));
    }


    // DB에 값이 존재하나
    private void assertAnnouncementExists(Long announcementId) {
        if (!announcementRepository.existsById(announcementId)) {
            throw new BusinessException(ErrorCode.ANNOUNCEMENT_NOT_FOUND);
        }
    }
}
