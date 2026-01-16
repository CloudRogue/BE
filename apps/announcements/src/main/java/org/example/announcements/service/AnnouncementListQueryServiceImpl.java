package org.example.announcements.service;

import lombok.RequiredArgsConstructor;
import org.example.announcements.api.AnnouncementSort;
import org.example.announcements.api.ApiListResponse;
import org.example.announcements.api.CursorMeta;
import org.example.announcements.domain.Announcement;
import org.example.announcements.dto.AnnouncementOpenItemResponse;
import org.example.announcements.repository.AnnouncementRepository;
import org.example.announcements.util.ScrollCursorCodec;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.KeysetScrollPosition;
import org.springframework.data.domain.Limit;
import org.springframework.data.domain.ScrollPosition;
import org.springframework.data.domain.Window;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class AnnouncementListQueryServiceImpl implements AnnouncementListQueryService {


    private final AnnouncementRepository announcementRepository;

    @Value("${announcements.paging.default-limit}")
    private int defaultLimit; // 디폴트값

    @Value("${announcements.paging.max-limit:50}")
    private int maxLimit; // 최대값


    // 접수중 공고 목록 조회 구현
    @Override
    public ApiListResponse<AnnouncementOpenItemResponse> getOpen(
            AnnouncementSort sort,
            String cursor,
            int limit
    ) {
        if (sort == null) sort = AnnouncementSort.DEADLINE;

        int safeLimit = safeLimit(limit);
        KeysetScrollPosition position = decode(cursor);
        LocalDate today = LocalDate.now();

        Window<Announcement> window =
                (sort == AnnouncementSort.LATEST)
                        ? announcementRepository
                        .findByStartDateLessThanEqualAndEndDateGreaterThanEqualOrderByCreatedAtDescIdDesc(
                                today, today, position, Limit.of(safeLimit)
                        )
                        : announcementRepository
                        .findByStartDateLessThanEqualAndEndDateGreaterThanEqualOrderByEndDateAscIdDesc(
                                today, today, position, Limit.of(safeLimit)
                        );

        return toResponse(window, safeLimit);
    }

    //접수전 공고 목록 조회
    @Override
    public ApiListResponse<AnnouncementOpenItemResponse> getUpcoming(AnnouncementSort sort, String cursor, int limit) {
        if (sort == null) sort = AnnouncementSort.DEADLINE;

        int safeLimit = safeLimit(limit);
        KeysetScrollPosition position = decode(cursor);
        LocalDate today = LocalDate.now();

        Window<Announcement> window =
                (sort == AnnouncementSort.LATEST)
                        ? announcementRepository
                        .findByStartDateGreaterThanOrderByCreatedAtDescIdDesc(
                                today, position, Limit.of(safeLimit)
                        )
                        : announcementRepository
                        .findByStartDateGreaterThanOrderByEndDateAscIdDesc(
                                today, position, Limit.of(safeLimit)
                        );

        return toResponse(window, safeLimit);
    }

    //마감  공고 조회
    @Override
    public ApiListResponse<AnnouncementOpenItemResponse> getClosed(String cursor, int limit) {
        int safeLimit = safeLimit(limit);
        KeysetScrollPosition position = decode(cursor);
        LocalDate today = LocalDate.now();

        Window<Announcement> window =
                announcementRepository.findByEndDateLessThanOrderByEndDateDescIdDesc(
                        today, position, Limit.of(safeLimit)
                );

        return toResponse(window, safeLimit);
    }

    // =====중복제거용 고통 메서드 생성하기==========
    // limit 보정
    private int safeLimit(int limit) {
        int requested = (limit <= 0) ? defaultLimit : limit;
        return clamp(requested, 1, Math.max(maxLimit, 1));
    }

    // cursor -> KeysetScrollPosition
    private KeysetScrollPosition decode(String cursor) {
        return ScrollCursorCodec.decodeOrThrow(cursor);
    }

    // Window -> ApiListResponse 변환
    private ApiListResponse<AnnouncementOpenItemResponse> toResponse(
            Window<Announcement> window,
            int safeLimit
    ) {
        List<AnnouncementOpenItemResponse> data = window.getContent().stream()
                .map(AnnouncementOpenItemResponse::from)
                .toList();

        boolean hasNext = window.hasNext();
        String nextCursor = buildNextCursor(window);

        return new ApiListResponse<>(
                data,
                new CursorMeta(safeLimit, hasNext, nextCursor)
        );
    }

    // 다음 페이지 커서 생성
    private String buildNextCursor(Window<?> window) {
        if (!window.hasNext() || window.isEmpty()) return null;


        ScrollPosition rawNext = window.positionAt(window.size() - 1);
        KeysetScrollPosition nextPos = ScrollCursorCodec.requireKeyset(rawNext);


        return ScrollCursorCodec.encode(nextPos);
    }

    // 값 보정
    private int clamp(int v, int min, int max) {
        return Math.min(Math.max(v, min), max);
    }
}
