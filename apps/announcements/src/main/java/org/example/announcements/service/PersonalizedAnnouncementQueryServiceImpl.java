package org.example.announcements.service;

import lombok.RequiredArgsConstructor;
import org.example.announcements.api.ApiListResponse;
import org.example.announcements.api.CursorMeta;
import org.example.announcements.api.PersonalizedSort;
import org.example.announcements.domain.Announcement;
import org.example.announcements.dto.AnnouncementSearchItemResponse;
import org.example.announcements.repository.AnnouncementRepository;
import org.example.announcements.service.internal.mypage.MypageClient;
import org.example.announcements.util.ScrollCursorCodec;
import org.springframework.data.domain.KeysetScrollPosition;
import org.springframework.data.domain.Limit;
import org.springframework.data.domain.ScrollPosition;
import org.springframework.data.domain.Window;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.function.Function;


import java.time.LocalDate;
import java.util.Collections;
import java.util.List;

import static org.example.announcements.util.AnnouncementStatusUtil.calcStatus;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class PersonalizedAnnouncementQueryServiceImpl implements PersonalizedAnnouncementQueryService {

    private final MypageClient mypageClient;
    private final AnnouncementRepository announcementRepository;

    @Override
    public ApiListResponse<AnnouncementSearchItemResponse> getPersonalized(
            String userId,
            String cursor,
            int limit,
            PersonalizedSort sort
    ) {

        // 마이페이지에서 추천후보 받기
        var personalized = mypageClient.getPersonalized(userId);

        // 후보추출
        List<Long> ids = (personalized == null || personalized.announcementIds() == null)
                ? Collections.emptyList()
                : personalized.announcementIds();


        if (ids.isEmpty()) {
            return new ApiListResponse<>(
                    Collections.emptyList(),
                    new CursorMeta(limit, false, null)
            );
        }

        // 정렬처리
        PersonalizedSort effectiveSort = (sort == null) ? PersonalizedSort.RELEVANCE : sort; // 기본값
        if (effectiveSort == PersonalizedSort.RELEVANCE) { // 적합순은 일단 최신순으로
            effectiveSort = PersonalizedSort.LATEST; // 최신순으로 치환
        }
        KeysetScrollPosition position = ScrollCursorCodec.decodeOrThrow(cursor); // null이면 initial


        Window<Announcement> window =
                (effectiveSort == PersonalizedSort.LATEST)
                        ? announcementRepository.findByIdInAndAdminCheckedTrueOrderByCreatedAtDescIdDesc(
                        ids,                     // 후보 id 풀
                        position,                // keyset position
                        Limit.of(limit)          // 페이지 크기
                )
                        : announcementRepository.findByIdInAndAdminCheckedTrueOrderByEndDateAscIdDesc(
                        ids,                     // 후보 id 풀
                        position,                // keyset position
                        Limit.of(limit)          // 페이지 크기
                );


        LocalDate today = LocalDate.now(); // 오늘

        // 변환
        return toResponse(
                window,
                limit,
                a -> {
                    String status = calcStatus(a.getStartDate(), a.getEndDate(), today);
                    return AnnouncementSearchItemResponse.of(a, status);
                }
        );
    }

    // Window -> ApiListResponse 변환
    private <T> ApiListResponse<T> toResponse(
            Window<Announcement> window,
            int limit,
            Function<Announcement, T> mapper
    ) {
        // content를 dto로 변환
        List<T> data = window.getContent().stream()
                .map(mapper) // 매핑
                .toList();   // 리스트화

        boolean hasNext = window.hasNext();
        String nextCursor = buildNextCursor(window);

        // 응답 조립
        return new ApiListResponse<>(
                data,
                new CursorMeta(
                        limit,
                        hasNext,
                        nextCursor
                )
        );
    }

    // 다음 커서 생성
    private String buildNextCursor(Window<?> window) {

        if (!window.hasNext() || window.isEmpty()) return null;
        ScrollPosition rawNext = window.positionAt(window.size() - 1); // 다음 포지션 후보
        KeysetScrollPosition nextPos = ScrollCursorCodec.requireKeyset(rawNext); // keyset 강제

        return ScrollCursorCodec.encode(nextPos);
    }


}
