package org.example.announcements.util;

import java.util.Map;

public record CursorPayload(
        String direction,
        Map<String, Object> keys
) { }
