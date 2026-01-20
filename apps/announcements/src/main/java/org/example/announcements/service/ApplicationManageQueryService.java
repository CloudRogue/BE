package org.example.announcements.service;

import org.example.announcements.api.ApplicationManageListResponse;

// 신청관리 조회 전용 서비스
public interface ApplicationManageQueryService {
    ApplicationManageListResponse getApplying(String userId, Long cursor, int size);
}
