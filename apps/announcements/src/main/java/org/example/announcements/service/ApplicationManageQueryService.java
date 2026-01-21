package org.example.announcements.service;

import org.example.announcements.api.ApplicationManageListResponse;

// 신청관리 조회 전용 서비스
public interface ApplicationManageQueryService {

    //신청관리 - 진행중
    ApplicationManageListResponse getApplying(String userId, Long cursor, int size);

    //신청관리 - 서류발표대기
    ApplicationManageListResponse getDocumentPending(String userId, Long cursor, int size);

    //신청관리 - 최종발표대기
    ApplicationManageListResponse getFinalPending(String userId, Long cursor, int size);

    //신청관리 - 발표완료
    ApplicationManageListResponse getClosed(String userId, Long cursor, int size);
}
