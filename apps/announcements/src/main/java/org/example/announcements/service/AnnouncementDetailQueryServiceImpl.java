package org.example.announcements.service;

import lombok.RequiredArgsConstructor;
import org.example.announcements.domain.Announcement;
import org.example.announcements.domain.AnnouncementSource;
import org.example.announcements.dto.AnnouncementDetailResponse;
import org.example.announcements.exception.BusinessException;
import org.example.announcements.exception.ErrorCode;
import org.example.announcements.repository.AnnouncementRepository;
import org.example.announcements.repository.AnnouncementScrapRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class AnnouncementDetailQueryServiceImpl implements AnnouncementDetailQueryService {

    private final AnnouncementRepository announcementRepository;
    private final AnnouncementScrapRepository announcementScrapRepository;

    @Value("${announcements.apply.lh}")
    private String lhApplyUrl;

    @Value("${announcements.apply.sh}")
    private String shApplyUrl;

    @Override
    public AnnouncementDetailResponse getDetail(Long announcementId, String userId) {
        Announcement a = announcementRepository.findById(announcementId) // PK로 공고 조회
                .orElseThrow(() -> new BusinessException(ErrorCode.ANNOUNCEMENT_NOT_FOUND));

        // 디데이 및 상태 계산
        LocalDate today = LocalDate.now();
        String status = calcStatus(a.getStartDate(), a.getEndDate(), today);
        Integer dDay = calcDDay(status, a.getEndDate(), today);

        Boolean isScrapped = null;
        if (userId != null && !userId.isBlank()) {
            isScrapped = announcementScrapRepository // 찜 테이블 조회
                    .existsByUserIdAndAnnouncement_Id(userId, announcementId); // 존재 여부로 찜 판단
        }

        String externalApplyUrl = calcExternalApplyUrl(a.getSource()); // source(LH/SH)에 따라 신청 링크를 고정값으로 선택

        return AnnouncementDetailResponse.of(a, status, dDay, isScrapped, externalApplyUrl);
    }

    // 공급 주체에따라 신청링크 제공
    private String calcExternalApplyUrl(AnnouncementSource source) {
        if (source == null) return null;
        return (source == AnnouncementSource.MYHOME) ? lhApplyUrl : shApplyUrl; // MYHOME(LH)면 LH 링크, 아니면 SH 링크
    }

    // status 계산하기
    private String calcStatus(LocalDate startDate, LocalDate endDate, LocalDate today) { // 공고 상태 계산
        if (startDate == null || endDate == null) return "CLOSED"; // 날짜 정보가 없으면 닫힘 처리(방어)

        if (today.isBefore(startDate)) return "UPCOMING"; // 오늘 < 시작일이면 접수 전
        if (today.isAfter(endDate)) return "CLOSED"; // 오늘 > 마감일이면 마감

        long d = ChronoUnit.DAYS.between(today, endDate); // 오늘~마감일까지 남은 일수

        if (d <= 3) return "DUE_SOON"; // 마감 3일 이내면 마감임박
        return "OPEN"; // 그 외는 접수중
    }

    // 남은 날짜계산하기
    private Integer calcDDay(String status, LocalDate endDate, LocalDate today) { // dDay 계산
        if (endDate == null) return null;
        if (!"OPEN".equals(status) && !"DUE_SOON".equals(status)) return null;
        return (int) ChronoUnit.DAYS.between(today, endDate);
    }
}
