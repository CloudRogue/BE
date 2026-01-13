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
public class AnnouncementQueryServiceImpl implements AnnouncementQueryService {

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

        // limit 보정
        int requested = (limit <= 0) ? defaultLimit : limit;
        int safeLimit = clamp(requested, 1, Math.max(maxLimit, 1));

        // 커서에서 키셋으로 변경
        KeysetScrollPosition position = ScrollCursorCodec.decodeOrThrow(cursor);


        Window<Announcement> window =
                (sort == AnnouncementSort.LATEST)
                        ? announcementRepository.scrollOpenLatest(LocalDate.now(), position, Limit.of(safeLimit))
                        : announcementRepository.scrollOpenDeadline(LocalDate.now(), position, Limit.of(safeLimit));

        // 엔티티 디티오로 변환
        List<AnnouncementOpenItemResponse> data = window.getContent().stream()
                .map(AnnouncementOpenItemResponse::from)
                .toList();

        // 다음 페이지 여부
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
