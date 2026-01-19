package org.example.announcements.dto;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.List;

@Getter
@RequiredArgsConstructor
public class AnnouncementOverviewResponse {

    private final Long announcementId;
    private final String content;       // 공고 내용
    private final String target;        // 대상
    private final List<String> regions; // 지역 리스트
    private final String applyMethod;   // 접수 방법

    public static AnnouncementOverviewResponse of(
            Long announcementId,
            String content,
            String target,
            List<String> regions,
            String applyMethod
    ) {
        return new AnnouncementOverviewResponse(announcementId, content, target, regions, applyMethod);
    }
}