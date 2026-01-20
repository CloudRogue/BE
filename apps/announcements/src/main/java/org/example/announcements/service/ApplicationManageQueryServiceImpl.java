package org.example.announcements.service;


import lombok.RequiredArgsConstructor;
import org.example.announcements.api.ApplicationManageListResponse;
import org.example.announcements.api.ApplicationManageMeta;
import org.example.announcements.dto.applicationmanage.ApplicationManageAnnouncementRow;
import org.example.announcements.dto.applicationmanage.ApplicationManageItemResponse;
import org.example.announcements.dto.applicationmanage.ApplicationManageSummaryCounts;
import org.example.announcements.exception.BusinessException;
import org.example.announcements.exception.ErrorCode;
import org.example.announcements.repository.AnnouncementApplicationRepository;
import org.example.announcements.util.AnnouncementStatusUtil;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

import static org.example.announcements.util.AnnouncementStatusUtil.calcApplicationManageDDay;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ApplicationManageQueryServiceImpl implements ApplicationManageQueryService {

    private final AnnouncementApplicationRepository announcementApplicationRepository;


    //신청관리 - 지원완료 후 진행중인 공고목록 조회
    @Override
    public ApplicationManageListResponse getApplying(String userId, Long cursor, int size) {

        if (userId == null || userId.isBlank()) {
            throw new BusinessException(ErrorCode.INVALID_REQUEST, "userId가 필요합니다.");
        }

        //기준일
        LocalDate today = LocalDate.now();

        //상단 서머리 조회
        ApplicationManageSummaryCounts summary =
                announcementApplicationRepository.countSummary(userId, today);

        int sizePlusOne = size + 1;

        //APPLYING 조회
        List<ApplicationManageAnnouncementRow> fetched =
                announcementApplicationRepository.findApplyingRows(userId, today, cursor, sizePlusOne);

        boolean hasNext = fetched.size() > size;

        List<ApplicationManageAnnouncementRow> content =
                hasNext ? fetched.subList(0, size) : fetched;

        //다음커서계산
        Long nextCursor = null;
        if (hasNext && !content.isEmpty()) {
            nextCursor = content.get(content.size() - 1).getAnnouncementId();
        }

        //아이템으로 변환하기(마감일 기준)
        List<ApplicationManageItemResponse> data = content.stream()
                .map(r -> {
                    //상태계산
                    String status = AnnouncementStatusUtil.calcApplicationManageStatus(
                            r.getEndDate(),
                            r.getDocumentPublishedAt(),
                            r.getFinalPublishedAt(),
                            today
                    );

                    //디데이계산
                    Long dDay = calcApplicationManageDDay(
                            status,
                            r.getEndDate(),
                            r.getDocumentPublishedAt(),
                            r.getFinalPublishedAt(),
                            today                       // 기준일
                    );


                    return new ApplicationManageItemResponse(
                            r.getAnnouncementId(), // announcementId
                            r.getTitle(),          // title
                            dDay,                  // dDay
                            r.getPublisher(),      // publisher
                            r.getHousingType(),    // housingType
                            status,            // currentStatus
                            r.getEndDate(),        // endDate
                            r.getDocumentPublishedAt(),                 // documentPublishedAt
                            r.getFinalPublishedAt()                     // finalPublishedAt
                    );
                })
                .toList();


        ApplicationManageMeta meta = new ApplicationManageMeta(
                nextCursor,
                hasNext,
                size
        );


        return new ApplicationManageListResponse(
                summary,
                data,
                meta
        );
    }
}
