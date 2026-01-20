package org.example.announcements.api;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class ApplicationManageMeta {

    private final Long nextCursor;
    private final boolean hasNext;
    private final int size;
}
