package org.example.announcements.service;

import lombok.RequiredArgsConstructor;
import org.example.announcements.api.AnnouncementSort;
import org.example.announcements.api.ApiListResponse;
import org.example.announcements.api.CursorMeta;
import org.example.announcements.domain.Announcement;
import org.example.announcements.dto.AnnouncementOpenItemResponse;
import org.example.announcements.repository.AnnouncementRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Limit;
import org.springframework.data.domain.ScrollPosition;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Window;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
public class AnnouncementQueryServiceImpl implements AnnouncementQueryService {

    private final AnnouncementRepository announcementRepository;

    @Value("${announcements.paging.default-limit}")
    private int defaultLimit; // 디폴트값

    @Value("${announcements.paging.max-limit:50}")
    private int maxLimit; // 최대값


    // 접수중 공고 목록 조회 구현
    @Override
    public ApiListResponse<AnnouncementOpenItemResponse> getOpen(AnnouncementSort sort, String cursor, int limit) {
        // 요청 limit 0 이하일경우 잡기
        int requested = (limit <= 0) ? defaultLimit : limit;
        int safeLimit = clamp(requested, 1, Math.max(maxLimit, 1));

        long offset = parseOffset(cursor);
        ScrollPosition position = ScrollPosition.offset(offset);
        Sort springSort = toSpringSort(sort);

        // 디비 스크롤 조회 실행
        Window<Announcement> window = announcementRepository.scrollOpen(
                LocalDate.now(),
                springSort,
                position,
                Limit.of(safeLimit)
        );

        // 디티오로 변환
        List<AnnouncementOpenItemResponse> data = window.getContent().stream()
                .map(AnnouncementOpenItemResponse::from)
                .toList();

        // 다음페이지 있는 지확인하고 계산
        boolean hasNext = window.hasNext();
        String nextCursor = hasNext ? String.valueOf(offset + data.size()) : null;

        return new ApiListResponse<>(
                data,
                new CursorMeta(safeLimit, hasNext, nextCursor)
        );


    }

    //AnnouncementSort -> Spring Sort로 변환
    private Sort toSpringSort(AnnouncementSort sort) {
        if (sort == null) sort = AnnouncementSort.DEADLINE;

        return switch (sort) {
            case LATEST -> Sort.by(
                    Sort.Order.desc("createdAt"),
                    Sort.Order.desc("id")
            );
            case DEADLINE -> Sort.by(
                    Sort.Order.asc("endDate"),
                    Sort.Order.desc("id")
            );
        };
    }

    //커서문자열 파싱
    private long parseOffset(String cursor) {
        if (cursor == null || cursor.isBlank()) return 0L;
        try {
            long v = Long.parseLong(cursor);
            return Math.max(v, 0L);
        } catch (NumberFormatException e) {
            return 0L;
        }
    }

    // 값 보정
    private int clamp(int v, int min, int max) {
        if (v < min) return min;
        return Math.min(v, max);
    }
}
