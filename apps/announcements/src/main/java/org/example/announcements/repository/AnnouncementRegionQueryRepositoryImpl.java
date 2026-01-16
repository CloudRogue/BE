package org.example.announcements.repository;

import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.example.announcements.api.AnnouncementSort;
import org.example.announcements.domain.Announcement;
import org.springframework.data.domain.KeysetScrollPosition;
import org.springframework.data.domain.Limit;
import org.springframework.data.domain.ScrollPosition;

import java.time.LocalDate;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import static org.example.announcements.domain.QAnnouncement.announcement;
import static org.example.announcements.domain.QAnnouncementRegion.announcementRegion;

@RequiredArgsConstructor
public class AnnouncementRegionQueryRepositoryImpl implements AnnouncementRegionQueryRepository {

    private final JPAQueryFactory queryFactory;


    @Override
    public RegionQueryResult findOpenByRegionKeyword(LocalDate today, String regionKeyword, AnnouncementSort sort, KeysetScrollPosition position, Limit limit) {
        int size = limit.max();


        //기본쿼리 접수중 공고 지역과 조인
        var q = queryFactory
                .selectDistinct(announcement)
                .from(announcement)
                .join(announcementRegion)
                .on(announcementRegion.announcement.eq(announcement))
                .where(
                        announcement.startDate.loe(today),
                        announcement.endDate.goe(today),
                        announcementRegion.regionName.containsIgnoreCase(regionKeyword)
                );

        //키셋 커서조건
        if (position != null && !position.isInitial()) {
            Map<String, Object> keys = position.getKeys();

            if (sort == AnnouncementSort.LATEST) {
                // 최신순
                q.where(
                        announcement.createdAt.lt(
                                (java.time.LocalDateTime) keys.get("createdAt")
                        ).or(
                                announcement.createdAt.eq(
                                        (java.time.LocalDateTime) keys.get("createdAt")
                                ).and(
                                        announcement.id.lt((Long) keys.get("id"))
                                )
                        )
                );
            } else {
                // 마감임박순
                q.where(
                        announcement.endDate.gt(
                                (LocalDate) keys.get("endDate")
                        ).or(
                                announcement.endDate.eq(
                                        (LocalDate) keys.get("endDate")
                                ).and(
                                        announcement.id.lt((Long) keys.get("id"))
                                )
                        )
                );
            }
        }

        //정렬
        if (sort == AnnouncementSort.LATEST) {
            q.orderBy(
                    announcement.createdAt.desc(),
                    announcement.id.desc()
            );
        } else {
            q.orderBy(
                    announcement.endDate.asc(),
                    announcement.id.desc()
            );
        }

        // 다음페이지 존재여부
        List<Announcement> fetched =
                q.limit(size + 1).fetch();

        boolean hasNext = fetched.size() > size;

        //실제반환 내용
        List<Announcement> content =
                hasNext ? fetched.subList(0, size) : fetched;

        // 다음페이지 커서 생성
        KeysetScrollPosition nextPosition = null;

        if (hasNext && !content.isEmpty()) {
            Announcement last = content.get(content.size() - 1);

            Map<String, Object> nextKeys = new LinkedHashMap<>();

            if (sort == AnnouncementSort.LATEST) {
                nextKeys.put("createdAt", last.getCreatedAt());
                nextKeys.put("id", last.getId());
            } else {
                nextKeys.put("endDate", last.getEndDate());
                nextKeys.put("id", last.getId());
            }

            nextPosition = ScrollPosition.forward(nextKeys);
        }


        return new RegionQueryResult(
                content,
                hasNext,
                nextPosition
        );
    }
}
