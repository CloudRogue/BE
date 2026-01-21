package org.example.announcements.service.internal.admin;

//어드민 통합저장입력을 받고 도메인을 없는값 만채우기 위한 유스케이스
public interface AdminAnnouncementEnrichUseCase {

    //어드민 통합저장으로 들어온 값을 이용하여 채우기
    void enrich(Long announcementId, AdminAnnouncementEnrichCommand command);
}
