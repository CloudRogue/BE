package org.example.mypage.activity.service;

import lombok.RequiredArgsConstructor;
import org.example.mypage.activity.api.AnnouncementsApi;
import org.example.mypage.activity.domain.Outbound;
import org.example.mypage.activity.dto.AnnouncementsResponse;
import org.example.mypage.activity.dto.response.OutboundResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * Outbound(최근 접속 공고 히스토리) 목록을 커서 기반으로 스크롤 조회하고,
 * announcementId 목록으로 공고 정보를 조회해 응답(OutboundResponse)을 구성합니다.
 *
 * <ul>
 *   <li>DB 조회는 {@link OutboundService}를 통해 size+1로 가져와 hasNext/nextCursor를 계산합니다.</li>
 *   <li>공고 정보는 {@link AnnouncementsApi#getAnnouncements(List)}로 조회 후
 *       announcementId 기준으로 매핑하고, 조회 순서(idsInOrder)를 보존해 응답을 생성합니다.</li>
 *   <li>공고가 누락된 id는 warn 로그 후 응답에서 제외합니다.</li>
 *   <li>공고 조회 결과가 announcementId 중복을 포함할 수 있다고 가정하면, 첫 번째 값을 유지합니다.</li>
 * </ul>
 *
 * <p><b>주의</b>: {@code announcementsApi} 호출은 외부 IO가 발생할 수 있으므로 트랜잭션 경계에 유의합니다.</p>
 */
@Component
@RequiredArgsConstructor
public class OutboundScrollFacade {

    private static final Logger log = LoggerFactory.getLogger(OutboundScrollFacade.class);

    private static final int MIN_LIMIT = 1;
    private static final int MAX_LIMIT = 50;

    private final OutboundService outboundService;
    private final AnnouncementsApi announcementsApi;


    public OutboundResponse getOutbound(String userId, Long cursor, int limit) {
        int size = normalizeLimit(limit);

        List<Outbound> outbounds = outboundService.fetchForScroll(userId, cursor, size + 1);

        boolean hasNext = outbounds.size() > size;
        if (hasNext) outbounds = outbounds.subList(0, size);

        Long nextCursor = outbounds.isEmpty() ? null : outbounds.getLast().getId();


        List<Long> idsInOrder = outbounds.stream()
                .map(Outbound::getAnnouncementId)
                .toList();

        if (idsInOrder.isEmpty()) {
            return new OutboundResponse(List.of(), nextCursor, hasNext);
        }


        Map<Long, Instant> viewedAtByAnnouncementId = outbounds.stream()
                .collect(Collectors.toMap(
                        Outbound::getAnnouncementId,
                        Outbound::getCreatedAt,
                        (a, b) -> a.isAfter(b) ? a : b
                ));

        List<AnnouncementsResponse> fetched = announcementsApi.getAnnouncements(idsInOrder);

        Map<Long, AnnouncementsResponse> map = fetched.stream()
                .collect(Collectors.toMap(
                        AnnouncementsResponse::announcementId,
                        Function.identity(),
                        (a, b) -> a
                ));

        List<OutboundResponse.Item> items = new ArrayList<>(idsInOrder.size());
        for (Long announcementId : idsInOrder) {
            AnnouncementsResponse a = map.get(announcementId);

            if (a == null) {
                log.warn("[OUTBOUND] announcement not found. userId={}, announcementId={}",
                        userId, announcementId);
                continue;
            }

            Instant viewedAt = viewedAtByAnnouncementId.get(announcementId);

            items.add(new OutboundResponse.Item(
                    a.announcementId(),
                    a.title(),
                    a.housingType(),
                    a.startDate(),
                    a.endDate(),
                    a.publishedAt(),
                    a.publisher(),
                    a.status(),
                    viewedAt
            ));
        }

        return new OutboundResponse(items, nextCursor, hasNext);
    }

    private int normalizeLimit(int limit) {
        return Math.min(Math.max(limit, MIN_LIMIT), MAX_LIMIT);
    }
}