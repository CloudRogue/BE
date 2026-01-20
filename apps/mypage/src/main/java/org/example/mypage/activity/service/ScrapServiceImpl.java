package org.example.mypage.activity.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.mypage.activity.repository.ScrapRepository;
import org.example.mypage.activity.domain.Scrap;
import org.example.mypage.exception.ScrapAlreadyExistsException;
import org.example.mypage.exception.ScrapNotFoundException;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;

/**
 * 스크랩 목록을 커서 기반으로 스크롤 조회합니다.
 *
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class ScrapServiceImpl implements ScrapService{
    private final ScrapRepository scrapRepository;


    /**
     * Scrap을 커서 기반으로 size+1 조회합니다.
     * <ul>
     *   <li>정렬: id DESC</li>
     *   <li>cursor가 있으면 {@code id < cursor} 조건으로 다음 페이지를 조회합니다.</li>
     *   <li>호출부에서 size+1을 넘기면 hasNext 판단에 사용할 수 있습니다.</li>
     * </ul>
     */
    @Transactional(readOnly = true)
    public List<Scrap> fetchForScroll(String userId, Long cursor, int sizePlusOne) {
        Pageable pageable = PageRequest.of(0, sizePlusOne, Sort.by(Sort.Direction.DESC, "id"));
        return scrapRepository.scrollByUserId(userId, cursor, pageable);
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

}
