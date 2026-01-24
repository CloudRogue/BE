package org.example.announcements.listener;

import java.util.List;

public interface AnnouncementApplicationForNotificationListener {


    //특정공고가 지원관리에 담긴 유저 목록 조회
    List<String> findApplicantUserIds(Long announcementId);
}
