package org.example.announcements.dto.applicationmanage;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

//신청관리 상단 카운트요약 dto
@Getter
@RequiredArgsConstructor
public class ApplicationManageSummaryCounts {
    private final long applyingCount;
    private final long documentWaitingCount;
    private final long finalWaitingCount;
    private final long closedCount;

    public static ApplicationManageSummaryCounts empty() {
        return new ApplicationManageSummaryCounts(0, 0, 0,0);
    }
}
