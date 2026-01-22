package org.example.announcements.service.internal.mypage;

import org.example.announcements.dto.internal.mypage.EligibilityDiagnoseRequest;
import org.example.announcements.dto.internal.mypage.MypageOutboundRequest;
import org.example.announcements.dto.internal.mypage.MypagePersonalizedResponse;
import org.example.announcements.dto.internal.mypage.MypageScrapRequest;

public interface MypageClient {

    //최근 열람/외부이동 기록 등록
    void postOutbound(MypageOutboundRequest request);

    //관심 공고 추가(스크랩)
    void postScrap(MypageScrapRequest request);

    //관심 공고 해제(스크랩)
    void deleteScrap(MypageScrapRequest request);

    //맞춤공고 목록 조회
    MypagePersonalizedResponse getPersonalized(String userId);


    EligibilityDiagnoseRequest getDiagnose(long announcementId, String userId);
}
