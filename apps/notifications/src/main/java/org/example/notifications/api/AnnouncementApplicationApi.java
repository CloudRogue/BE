package org.example.notifications.api;

import org.example.notifications.dto.api.AppliedUserIds;

import java.util.List;

//알림 -> 공고
public interface AnnouncementApplicationApi {

    //특정공고를 지원한 유저들의 목록을 가져오기
    AppliedUserIds findApplicationUserIds(Long announcementId);
}
