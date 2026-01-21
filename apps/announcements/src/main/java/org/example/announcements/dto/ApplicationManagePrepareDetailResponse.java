package org.example.announcements.dto;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Getter
@RequiredArgsConstructor
public class ApplicationManagePrepareDetailResponse {


    private final Long announcementId;


    private final String title; //공고제목


    private final Long dDay;


    private final String publisher;


    private final String housingType;

    //현재 상태
    private final String currentStatus;

    // 공고접수마감일
    private final LocalDate endDate;

    //신청링크
    private final String applyUrl;

    // 접수단계 정보
    private final ApplySection apply;

    // 서류발표단계 정보
    private final DocResultSection docResult;

    // 최종 발표단계 정보
    private final FinalResultSection finalResult;

    //접수단계
    @Getter
    @RequiredArgsConstructor
    public static class ApplySection {
        private final LocalDate date;
        private final List<DocumentItem> documents;
    }

    //서류발표
    @Getter
    @RequiredArgsConstructor
    public static class DocResultSection{
        private final LocalDate date;
        private final List<DocumentItem> documents;
    }

    //최종발표
    @Getter
    @RequiredArgsConstructor
    public static class FinalResultSection {
        private final LocalDate date;
        private final String summary;
    }

    // 단계별 제출서류 아이템
    @Getter
    @RequiredArgsConstructor
    public static class DocumentItem {
        private final Long id;
        private final String name;
        private final String scope; // COMMON 이나 TARGET_ONLY
    }
}
