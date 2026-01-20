package org.example.mypage.activity.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.mypage.activity.api.AnnouncementsApi;
import org.example.mypage.activity.domain.Outbound;
import org.example.mypage.activity.dto.AnnouncementsResponse;
import org.example.mypage.activity.dto.response.OutboundResponse;
import org.example.mypage.activity.repository.OutboundRepository;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;


@Slf4j
@Service
@RequiredArgsConstructor
public class OutBoundServiceImpl implements OutboundService {
    private final OutboundRepository outboundRepository;

    /**
     * Outbound(최근 접속 공고 히스토리)을 커서 기반으로 size+1 조회합니다.
     * <ul>
     *   <li>정렬: id DESC</li>
     *   <li>cursor가 있으면 {@code id < cursor} 조건으로 다음 페이지를 조회합니다.</li>
     *   <li>호출부에서 size+1을 넘기면 hasNext 판단에 사용할 수 있습니다.</li>
     * </ul>
     */
    @Override
    @Transactional(readOnly = true)
    public List<Outbound> fetchForScroll(String userId, Long cursor, int sizePlusOne) {
        Pageable pageable = PageRequest.of(
                0,
                sizePlusOne,
                Sort.by(Sort.Direction.DESC, "id")
        );
        return outboundRepository.scrollByUserId(userId, cursor, pageable);
    }

    /**
     * 사용자의 공고 접속 이력을 저장합니다.
     * <p>히스토리 성격이므로 동일 공고를 다시 접속해도 새 레코드가 추가될 수 있습니다.</p>
     */
    @Override
    @Transactional
    public void recordOutbound(String userId, Long announcementId) {
        outboundRepository.save(Outbound.create(userId, announcementId));
    }
}
