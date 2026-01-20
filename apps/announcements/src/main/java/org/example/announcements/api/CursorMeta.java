package org.example.announcements.api;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class CursorMeta {

    private final int limit; // 페이지 크기
    private final boolean hasNext; // 다음 페이지 존재 여부
    private final String nextCursor; // 다음 페이지 조회용 커서

}
