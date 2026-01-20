package org.example.mypage.activity.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.mypage.activity.repository.ScrapRepository;
import org.example.mypage.activity.api.AnnouncementsApi;
import org.example.mypage.activity.domain.Scrap;
import org.example.mypage.activity.dto.AnnouncementsResponse;
import org.example.mypage.activity.dto.response.ScrapResponse;
import org.example.mypage.exception.ScrapAlreadyExistsException;
import org.example.mypage.exception.ScrapNotFoundException;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;


/**
 * 스크랩 목록을 커서 기반으로 스크롤 조회합니다.
 *
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class ScrapServiceImpl implements ScrapService{
    private static final int MIN_LIMIT = 1;
    private static final int MAX_LIMIT = 50;

    private final ScrapRepository scrapRepository;
    private final AnnouncementsApi announcementsApi;

    /**
     * 스크랩 목록을 커서 기반으로 스크롤 조회합니다.
     *
     * <h3>공고 정보 매핑</h3>
     * <ul>
     *   <li>스크랩에는 {@code announcementId}만 저장되어 있으므로, 조회된 id 목록으로 공고 정보를 함께 가져옵니다.</li>
     *   <li>공고 조회 결과는 {@code announcementId} 기준으로 맵핑한 뒤, 스크랩 조회 순서(idsInOrder)를 보존하여 응답을 구성합니다.</li>
     *   <li>공고가 조회되지 않는(삭제/미존재 등) id는 로그를 남기고 응답에서 제외합니다.</li>
     * </ul>
     *
     * <p><b>주의</b>:</p>
     * <ul>
     *   <li>본 메서드에서 호출하는 {@code announcementsApi.getAnnouncements(...)} 는 실제 네트워크 IO가 발생하지 않습니다.</li>
     * </ul>
     */
    @Transactional(readOnly = true)
    public ScrapResponse getScraps(String userId, Long cursor, int limit) {
        int size = normalizeLimit(limit);
        Pageable pageable = buildPageable(size);

        // 1) 커서 스크롤 조회(size+1)
        List<Scrap> scraps = scrapRepository.scrollByUserId(userId, cursor, pageable);

        boolean hasNext = scraps.size() > size;
        if (hasNext) scraps = scraps.subList(0, size);

        Long nextCursor = scraps.isEmpty() ? null : scraps.getLast().getId();

        // 2) announcementId 순서 보존 리스트
        List<Long> idsInOrder = scraps.stream()
                .map(Scrap::getAnnouncementId)
                .toList();

        if (idsInOrder.isEmpty()) {
            return new ScrapResponse(List.of(), nextCursor, hasNext);
        }

        // 3) 공고 조회 + announcementId로 매핑
        List<AnnouncementsResponse> fetched = announcementsApi.getAnnouncements(idsInOrder);

        // 정책: announcementId 중복 반환 가능 -> 첫 값 유지
        Map<Long, AnnouncementsResponse> map = fetched.stream()
                .collect(Collectors.toMap(
                        AnnouncementsResponse::announcementId,
                        Function.identity(),
                        (a, b) -> a
                ));

        // 4) idsInOrder 순서대로 응답 구성(누락은 warn + 제외)
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

    /**
     * 스크랩(즐겨찾기) 추가 서비스.
     *
     * @param userId 사용자 ID(ULID)
     * @param announcementId 공고 ID
     * @throws ScrapAlreadyExistsException 이미 스크랩된 공고인 경우
     */
    @Override
    @Transactional
    public void addScrap(String userId, Long announcementId) {
        if (scrapRepository.existsByUserIdAndAnnouncementId(userId, announcementId)) {
            throw new ScrapAlreadyExistsException();
        }

        Scrap scrap = Scrap.create(userId, announcementId);
        scrapRepository.save(scrap);
    }

    /**
     * 스크랩(즐겨찾기) 삭제 서비스.
     *
     * @param userId 인증된 사용자 ID(ULID)
     * @param announcementId 공고 ID
     * @throws ScrapNotFoundException 해당 공고가 스크랩되어 있지 않은 경우
     */
    @Override
    @Transactional
    public void deleteScraps(String userId, Long announcementId) {
        int deleted = scrapRepository.deleteByUserIdAndAnnouncementId(userId, announcementId);

        if (deleted == 0) {
            throw new ScrapNotFoundException();
        }
    }


    private int normalizeLimit(int limit) {
        return Math.min(Math.max(limit, MIN_LIMIT), MAX_LIMIT);
    }

    private Pageable buildPageable(int size) {
        return PageRequest.of(0, size + 1, Sort.by(Sort.Direction.DESC, "id"));
    }

}
