package org.example.mypage.activity.service;

import lombok.RequiredArgsConstructor;
import org.example.mypage.activity.api.AnnouncementsApi;
import org.example.mypage.activity.domain.Scrap;
import org.example.mypage.activity.dto.AnnouncementsResponse;
import org.example.mypage.activity.dto.response.ScrapResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * Scrap(즐겨찾기) 목록을 커서 기반으로 스크롤 조회하고,
 * 스크랩에 저장된 announcementId 목록으로 공고 정보를 조회해 ScrapResponse를 구성합니다.
 *
 * <ul>
 *   <li>DB 조회는 {@link ScrapService#fetchForScroll(String, Long, int)} 를 통해 size+1로 가져와
 *       hasNext/nextCursor를 계산합니다.</li>
 *   <li>공고 정보는 {@link AnnouncementsApi#getAnnouncements(List)} 로 조회 후
 *       announcementId 기준으로 매핑하고, 스크랩 조회 순서(idsInOrder)를 보존해 응답을 생성합니다.</li>
 *   <li>공고가 누락된 id는 warn 로그 후 응답에서 제외합니다.</li>
 *   <li>공고 조회 결과가 announcementId 중복을 포함할 수 있다고 가정하면, 첫 번째 값을 유지합니다.</li>
 * </ul>
 *
 * <p><b>주의</b>: {@code announcementsApi} 호출은 외부 IO가 발생할 수 있으므로 트랜잭션 경계에 유의합니다.</p>
 */
@Component
@RequiredArgsConstructor
public class ScrapScrollFacade {

    private static final Logger log = LoggerFactory.getLogger(ScrapScrollFacade.class);

    private static final int MIN_LIMIT = 1;
    private static final int MAX_LIMIT = 50;

    private final ScrapServiceImpl scrapService; // 인터페이스로 빼고 싶으면 ScrapService에 fetchForScroll 노출
    private final AnnouncementsApi announcementsApi;

    public ScrapResponse getScraps(String userId, Long cursor, int limit) {
        int size = normalizeLimit(limit);


        List<Scrap> rows = scrapService.fetchForScroll(userId, cursor, size + 1);

        boolean hasNext = rows.size() > size;
        if (hasNext) rows = rows.subList(0, size);

        Long nextCursor = rows.isEmpty() ? null : rows.getLast().getId();


        List<Long> idsInOrder = rows.stream()
                .map(Scrap::getAnnouncementId)
                .toList();

        if (idsInOrder.isEmpty()) {
            return new ScrapResponse(List.of(), nextCursor, hasNext);
        }


        List<AnnouncementsResponse> fetched = announcementsApi.getAnnouncements(idsInOrder);
        Map<Long, AnnouncementsResponse> map = fetched.stream()
                .collect(Collectors.toMap(
                        AnnouncementsResponse::announcementId,
                        Function.identity(),
                        (a, b) -> a
                ));


        List<ScrapResponse.Item> items = new ArrayList<>(idsInOrder.size());
        for (Long announcementId : idsInOrder) {
            AnnouncementsResponse a = map.get(announcementId);

            if (a == null) {
                log.warn("[SCRAP] announcement not found from external. userId={}, announcementId={}",
                        userId, announcementId);
                continue;
            }

            items.add(new ScrapResponse.Item(
                    a.announcementId(),
                    a.title(),
                    a.housingType(),
                    a.startDate(),
                    a.endDate(),
                    a.publishedAt(),
                    a.publisher(),
                    a.status()
            ));
        }

        return new ScrapResponse(items, nextCursor, hasNext);
    }

    private int normalizeLimit(int limit) {
        return Math.min(Math.max(limit, MIN_LIMIT), MAX_LIMIT);
    }
}
