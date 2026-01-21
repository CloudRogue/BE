package org.example.announcements.service;

import lombok.RequiredArgsConstructor;
import org.example.announcements.domain.Announcement;
import org.example.announcements.domain.AnnouncementSource;
import org.example.announcements.dto.AnnouncementDetailResponse;
import org.example.announcements.exception.BusinessException;
import org.example.announcements.exception.ErrorCode;
import org.example.announcements.repository.AnnouncementRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;


import static org.example.announcements.util.AnnouncementStatusUtil.calcDDay;
import static org.example.announcements.util.AnnouncementStatusUtil.calcStatus;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class AnnouncementDetailQueryServiceImpl implements AnnouncementDetailQueryService {

    private final AnnouncementRepository announcementRepository;

    @Override
    public AnnouncementDetailResponse getDetail(Long announcementId, String userId) {
        Announcement a = announcementRepository.findByIdAndAdminCheckedTrue(announcementId) // PK로 공고 조회
                .orElseThrow(() -> new BusinessException(ErrorCode.ANNOUNCEMENT_NOT_FOUND));

        // 디데이 및 상태 계산
        LocalDate today = LocalDate.now();
        String status = calcStatus(a.getStartDate(), a.getEndDate(), today);
        Integer dDay = calcDDay(status, a.getEndDate(), today);

        Boolean isScrapped = false;

        //추후에 서비스 코드 수정예정
//        if (userId != null && !userId.isBlank()) {
//            isScrapped = announcementScrapRepository // 찜 테이블 조회
//                    .existsByUserIdAndAnnouncement_Id(userId, announcementId); // 존재 여부로 찜 판단
//        }


        return AnnouncementDetailResponse.of(a, status, dDay, isScrapped);
    }



}
