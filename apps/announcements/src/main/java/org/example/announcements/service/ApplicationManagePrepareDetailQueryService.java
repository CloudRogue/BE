package org.example.announcements.service;

import org.example.announcements.dto.ApplicationManagePrepareDetailResponse;


public interface ApplicationManagePrepareDetailQueryService {

    ApplicationManagePrepareDetailResponse getDetail(Long announcementId);
}
