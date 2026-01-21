package org.example.announcements.service;

import lombok.RequiredArgsConstructor;
import org.example.announcements.api.AnnouncementSort;
import org.example.announcements.api.ApiListResponse;
import org.example.announcements.api.CursorMeta;
import org.example.announcements.domain.Announcement;
import org.example.announcements.dto.AnnouncementOpenItemResponse;
import org.example.announcements.dto.AnnouncementSearchItemResponse;
import org.example.announcements.repository.AnnouncementRepository;
import org.example.announcements.repository.RegionQueryResult;
import org.example.announcements.util.ScrollCursorCodec;
import org.springframework.data.domain.KeysetScrollPosition;
import org.springframework.data.domain.Limit;
import org.springframework.data.domain.ScrollPosition;
import org.springframework.data.domain.Window;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.function.Function;

import static org.example.announcements.util.AnnouncementStatusUtil.calcStatus;


@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class AnnouncementListQueryServiceImpl implements AnnouncementListQueryService {


    private final AnnouncementRepository announcementRepository;


    // 접수중 공고 목록 조회 구현
    @Override
    public ApiListResponse<AnnouncementOpenItemResponse> getOpen(
            AnnouncementSort sort,
            String cursor,
            int limit
    ) {
        if (sort == null) sort = AnnouncementSort.DEADLINE;

        KeysetScrollPosition position = decode(cursor);
        LocalDate today = LocalDate.now();

        Window<Announcement> window =
                (sort == AnnouncementSort.LATEST)
                        ? announcementRepository
                        .findByAdminCheckedTrueAndStartDateLessThanEqualAndEndDateGreaterThanEqualOrderByCreatedAtDescIdDesc(
                                today, today, position, Limit.of(limit)
                        )
                        : announcementRepository
                        .findByAdminCheckedTrueAndStartDateLessThanEqualAndEndDateGreaterThanEqualOrderByEndDateAscIdDesc(
                                today, today, position, Limit.of(limit)
                        );

        return toResponse(window, limit);
    }

    //접수전 공고 목록 조회
    @Override
    public ApiListResponse<AnnouncementOpenItemResponse> getUpcoming(AnnouncementSort sort, String cursor, int limit) {
        if (sort == null) sort = AnnouncementSort.DEADLINE;


        KeysetScrollPosition position = decode(cursor);
        LocalDate today = LocalDate.now();

        Window<Announcement> window =
                (sort == AnnouncementSort.LATEST)
                        ? announcementRepository
                        .findByAdminCheckedTrueAndStartDateGreaterThanOrderByCreatedAtDescIdDesc(
                                today, position, Limit.of(limit)
                        )
                        : announcementRepository
                        .findByAdminCheckedTrueAndStartDateGreaterThanOrderByEndDateAscIdDesc(
                                today, position, Limit.of(limit)
                        );

        return toResponse(window, limit);
    }

    //마감  공고 조회
    @Override
    public ApiListResponse<AnnouncementOpenItemResponse> getClosed(String cursor, int limit) {

        KeysetScrollPosition position = decode(cursor);
        LocalDate today = LocalDate.now();

        Window<Announcement> window =
                announcementRepository.findByAdminCheckedTrueAndEndDateLessThanOrderByEndDateDescIdDesc(
                        today, position, Limit.of(limit)
                );

        return toResponse(window, limit);
    }

    // 접수중인 공고 목록을 발행처로 검색
    @Override
    public ApiListResponse<AnnouncementSearchItemResponse> getOpenByPublisher(
            AnnouncementSort sort,
            String publisher,
            String cursor,
            int limit
    ) {
        if (sort == null) sort = AnnouncementSort.DEADLINE;

        KeysetScrollPosition position = decode(cursor);
        LocalDate today = LocalDate.now();

        Window<Announcement> window =
                (sort == AnnouncementSort.LATEST)
                        ? announcementRepository
                        .findByAdminCheckedTrueAndStartDateLessThanEqualAndEndDateGreaterThanEqualAndPublisherContainingIgnoreCaseOrderByCreatedAtDescIdDesc(
                                today,
                                today,
                                publisher,
                                position,
                                Limit.of(limit)
                        )
                        : announcementRepository
                        .findByAdminCheckedTrueAndStartDateLessThanEqualAndEndDateGreaterThanEqualAndPublisherContainingIgnoreCaseOrderByEndDateAscIdDesc(
                                today,
                                today,
                                publisher,
                                position,
                                Limit.of(limit)
                        );

        //  제네릭 toResponse 사용
        return toResponse(window, limit, a -> {
            String status = calcStatus(a.getStartDate(), a.getEndDate(), today);
            return AnnouncementSearchItemResponse.of(a, status);
        });
    }

    @Override
    public ApiListResponse<AnnouncementSearchItemResponse> getOpenByHousingType(AnnouncementSort sort, String housingType, String cursor, int limit) {
        if (sort == null) sort = AnnouncementSort.DEADLINE;

        KeysetScrollPosition position = decode(cursor);
        LocalDate today = LocalDate.now();

        // 정렬 기준에 따라 쿼리 분기
        Window<Announcement> window =
                (sort == AnnouncementSort.LATEST)
                        ? announcementRepository
                        .findByAdminCheckedTrueAndStartDateLessThanEqualAndEndDateGreaterThanEqualAndHousingTypeContainingIgnoreCaseOrderByCreatedAtDescIdDesc(
                                today,
                                today,
                                housingType,
                                position,
                                Limit.of(limit)
                        )
                        : announcementRepository
                        .findByAdminCheckedTrueAndStartDateLessThanEqualAndEndDateGreaterThanEqualAndHousingTypeContainingIgnoreCaseOrderByEndDateAscIdDesc(
                                today,
                                today,
                                housingType,
                                position,
                                Limit.of(limit)
                        );

        // Window<Announcement> -> ApiListResponse<AnnouncementSearchItemResponse>
        return toResponse(window, limit, a -> {
            String status = calcStatus(a.getStartDate(), a.getEndDate(), today);
            return AnnouncementSearchItemResponse.of(a, status);
        });
    }

    @Override
    public ApiListResponse<AnnouncementSearchItemResponse> getOpenByRegion(String region, AnnouncementSort sort, String cursor, int limit) {
        if (sort == null) sort = AnnouncementSort.DEADLINE;

        KeysetScrollPosition position = decode(cursor);
        LocalDate today = LocalDate.now();

        RegionQueryResult result =
                announcementRepository.findOpenByRegionKeyword(
                        today,
                        region,
                        sort,
                        position,
                        Limit.of(limit)
                );

        String nextCursor = ScrollCursorCodec.encode(result.nextPosition());

        return new ApiListResponse<>(
                result.content().stream()
                        .map(a ->
                                AnnouncementSearchItemResponse.of(
                                        a,
                                        calcStatus(a.getStartDate(), a.getEndDate(), today)
                                )
                        )
                        .toList(),
                new CursorMeta(
                        limit,
                        result.hasNext(),
                        nextCursor
                )
        );
    }

    // =====중복제거용 고통 메서드 생성하기==========


    // cursor -> KeysetScrollPosition
    private KeysetScrollPosition decode(String cursor) {
        return ScrollCursorCodec.decodeOrThrow(cursor);
    }

    // Window -> ApiListResponse 변환
    private ApiListResponse<AnnouncementOpenItemResponse> toResponse(
            Window<Announcement> window,
            int limit
    ) {
        return toResponse(window, limit, AnnouncementOpenItemResponse::from);
    }

    // publisher,housing,region전용
    private <T> ApiListResponse<T> toResponse(
            Window<Announcement> window,
            int limit,
            Function<Announcement, T> mapper
    ) {
        List<T> data = window.getContent().stream()
                .map(mapper)
                .toList();

        boolean hasNext = window.hasNext();
        String nextCursor = buildNextCursor(window);

        return new ApiListResponse<>(
                data,
                new CursorMeta(limit, hasNext, nextCursor)
        );
    }

    // 다음 페이지 커서 생성
    private String buildNextCursor(Window<?> window) {
        if (!window.hasNext() || window.isEmpty()) return null;


        ScrollPosition rawNext = window.positionAt(window.size() - 1);
        KeysetScrollPosition nextPos = ScrollCursorCodec.requireKeyset(rawNext);


        return ScrollCursorCodec.encode(nextPos);
    }

}
