package org.example.announcements.repository;

import org.example.announcements.domain.Announcement;
import org.springframework.data.domain.KeysetScrollPosition;

import java.util.List;

//스프링 4.x부터는 Querydsl 커스텀 쿼리에서 윈도우를 직접만들수없음
public record RegionQueryResult(
        List<Announcement> content,
        boolean hasNext,
        KeysetScrollPosition nextPosition
) {}