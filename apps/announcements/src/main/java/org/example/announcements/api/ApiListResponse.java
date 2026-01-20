package org.example.announcements.api;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.List;

@Getter
@RequiredArgsConstructor
public class ApiListResponse<T> {

    private final List<T> data; // 목록 데이터
    private final CursorMeta meta; // 페이지네이션
}
