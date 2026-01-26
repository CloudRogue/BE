package org.example.announcements.port;

import java.time.LocalDate;
import java.util.List;

public record AdminAnnouncementEnrichCommand(

        String publisher,
        String housingType,
        String supplyType,
        String regionCode,
        String regionName,

        String applyUrl,
        String applyEntryUrl,
        Long rentGtn,
        Long enty,
        Long prtpay,
        Long surlus,
        Long mtRntchrg,

        Submission submission,

        OverviewSummary overviewSummary
) {



    //제출 관련정보
    public record Submission(
        Dates dates, //날짜 관련된거
        List<DocumentInput> applyDocuments, //phase = APPLY인거
        List<DocumentInput> atDocument // phase = DOC_RESULT인거
    ){}

    //날짜
    public record Dates(
            LocalDate applyStartDate, //시작
            LocalDate applyEndDate, //마감
            LocalDate documentPublishedAt, //서류발표
            LocalDate finalPublishedAt // 최종발표
    ){}

    //제출서류
    public record DocumentInput(
            String name,
            String scope
    ){}



    //오버뷰쪽

    //개요/요약
    public record OverviewSummary(
            Overview overview,
            String summary
    ){}

    //공고개요 입력
    public record Overview(
            String content,
            String target,
            List<String> regions, //지역
            String applyMethod
    ){}
}
