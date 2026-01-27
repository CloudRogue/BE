package org.example.announcements.service;

import org.example.announcements.dto.ApplicationManagePrepareDetailResponse;


public interface ApplicationManagePrepareDetailQueryService {

    ApplicationManagePrepareDetailResponse getDetail(String userId, Long announcementId);
}
