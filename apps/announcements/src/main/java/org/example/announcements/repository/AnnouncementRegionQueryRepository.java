package org.example.announcements.repository;

import org.example.announcements.api.AnnouncementSort;
import org.example.announcements.domain.Announcement;
import org.springframework.data.domain.KeysetScrollPosition;
import org.springframework.data.domain.Limit;
import org.springframework.data.domain.Window;

import java.time.LocalDate;

//지역 조인 기반 검색 전용 커스텀
public interface AnnouncementRegionQueryRepository {

    //접수중이고 지역키워드로 검색
    RegionQueryResult findOpenByRegionKeyword(
            LocalDate today,
            String regionKeyword,
            AnnouncementSort sort,
            KeysetScrollPosition position,
            Limit limit
    );
}
