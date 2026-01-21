package org.example.announcements.api;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.example.announcements.dto.applicationmanage.ApplicationManageItemResponse;
import org.example.announcements.dto.applicationmanage.ApplicationManageSummaryCounts;

import java.util.List;

@Getter
@RequiredArgsConstructor
public class ApplicationManageListResponse {

    private final ApplicationManageSummaryCounts summary;
    private final List<ApplicationManageItemResponse> data;
    private final ApplicationManageMeta meta;
}
