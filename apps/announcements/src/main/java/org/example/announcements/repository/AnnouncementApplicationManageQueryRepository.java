package org.example.announcements.repository;

import org.example.announcements.dto.applicationmanage.ApplicationManageAnnouncementRow;
import org.example.announcements.dto.applicationmanage.ApplicationManageSummaryCounts;

import java.time.LocalDate;
import java.util.List;

public interface AnnouncementApplicationManageQueryRepository {

    //지원중 목록 조회(유저가 지원 완료한 공고중 endDate >= today인거)
    List<ApplicationManageAnnouncementRow> findApplyingRows(String userId, LocalDate today, Long cursor, int sizePlusOne);

    //서류 발표 대기 목록 조회( endDate < today < documentPublishedAt)
    List<ApplicationManageAnnouncementRow> findDocumentWaitingRows(String userId, LocalDate today, Long cursor, int sizePlusOne);

    //최종발표 대기 목록 조회( documentPublishedAt <= today < finalPublishedAt)
    List<ApplicationManageAnnouncementRow> findFinalWaitingRows(String userId, LocalDate today, Long cursor, int sizePlusOne);

    //발표완료 목록 조회( finalPublishedAt <= today)
    List<ApplicationManageAnnouncementRow> findClosedRows(String userId, LocalDate today, Long cursor, int sizePlusOne);

    //상단 탭 카운트 요약
    ApplicationManageSummaryCounts countSummary(String userId, LocalDate today);
}
