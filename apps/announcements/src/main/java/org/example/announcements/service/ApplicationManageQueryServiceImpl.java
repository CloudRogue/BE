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
        return getByFixedStatus(
                userId,
                cursor,
                size,
                "APPLYING",
                (today, cur, sizePlusOne) -> announcementApplicationRepository.findApplyingRows(
                        userId,
                        today,
                        cur,
                        sizePlusOne
                )
        );
    }

    //신청관리 - 지원완료 후 서류발표대기 공고목록 조회
    @Override
    public ApplicationManageListResponse getDocumentPending(String userId, Long cursor, int size) {
        return getByFixedStatus(
                userId,
                cursor,
                size,
                "DOCUMENT_WAITING",
                (today, cur, sizePlusOne) -> announcementApplicationRepository.findDocumentWaitingRows(
                        userId,
                        today,
                        cur,
                        sizePlusOne
                )
        );
    }

    //신청관리 - 지원완료 후 최종발표대기 공고목록 조회
    @Override
    public ApplicationManageListResponse getFinalPending(String userId, Long cursor, int size) {
        return getByFixedStatus(
                userId,
                cursor,
                size,
                "FINAL_WAITING",
                (today, cur, sizePlusOne) -> announcementApplicationRepository.findFinalWaitingRows(
                        userId,
                        today,
                        cur,
                        sizePlusOne
                )
        );
    }

    //신청관리 - 지원완료 후 발표마감된 공고목록 조회
    @Override
    public ApplicationManageListResponse getClosed(String userId, Long cursor, int size) {
        return getByFixedStatus(
                userId,
                cursor,
                size,
                "CLOSED",
                (today, cur, sizePlusOne) -> announcementApplicationRepository.findClosedRows(
                        userId,
                        today,
                        cur,
                        sizePlusOne
                )
        );
    }

    //공통 템플릿 메서드
    private ApplicationManageListResponse getByFixedStatus(String userId, Long cursor, int size, String fixedStatus, TriFunction<LocalDate, Long, Integer, List<ApplicationManageAnnouncementRow>> rowsFetcher){

        // userId 검증
        if (userId == null || userId.isBlank()) {
            throw new BusinessException(ErrorCode.INVALID_REQUEST, "userId가 필요합니다.");
        }

        LocalDate today = LocalDate.now();

        //서머리 조회
        ApplicationManageSummaryCounts summary = announcementApplicationRepository.countSummary(userId, today);

        int sizePlusOne = size + 1;
        //상태별로 로우 조회
        List<ApplicationManageAnnouncementRow> fetched = rowsFetcher.apply(today, cursor, sizePlusOne);

        boolean hasNext = fetched.size() > size;
        List<ApplicationManageAnnouncementRow> content = hasNext ? fetched.subList(0, size) : fetched;

        //넥스트 커서 계산
        Long nextCursor = null;
        if (hasNext && !content.isEmpty()) {
            nextCursor = content.get(content.size() - 1).getAnnouncementId();
        }

        //아이템으로 변환
        List<ApplicationManageItemResponse> data = content.stream()
                .map(r -> {

                    Long dDay = calcApplicationManageDDay( // dDay 계산
                            fixedStatus,
                            r.getEndDate(),
                            r.getDocumentPublishedAt(),
                            r.getFinalPublishedAt(),
                            today
                    );

                    return new ApplicationManageItemResponse( // item 응답 생성
                            r.getAnnouncementId(),
                            r.getTitle(),
                            dDay,
                            r.getPublisher(),
                            r.getSupplyType(),
                            fixedStatus,
                            r.getEndDate(),
                            r.getDocumentPublishedAt(),
                            r.getFinalPublishedAt()
                    );

                })
                .toList();

        //메타생성
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


    // 함수형 인터페이스 설계
    @FunctionalInterface
    private interface TriFunction<A, B, C, R> {
        R apply(A a, B b, C c);
    }


}
