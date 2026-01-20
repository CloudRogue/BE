package org.example.mypage.activity.service;

import org.example.mypage.activity.domain.Outbound;

import java.util.List;

/**
 * Outbound(최근 접속 공고 히스토리) 도메인 서비스.
 *
 * <ul>
 *   <li>fetchForScroll: userId 기준으로 Outbound를 커서 기반(size+1)으로 조회하기 위한 DB 전용 메서드입니다.
 *       (id DESC, cursor는 마지막 id 기준으로 사용)</li>
 *   <li>recordOutbound: 사용자의 공고 접속 이력을 저장합니다.
 *       히스토리 성격이므로 동일 공고 재접속 시에도 새 레코드가 추가될 수 있습니다.</li>
 * </ul>
 */
public interface OutboundService {
    List<Outbound> fetchForScroll(String userId, Long cursor, int sizePlusOne);
    void recordOutbound(String userId, Long announcementId);
}
