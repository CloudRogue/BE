package org.example.announcements.util;

import org.springframework.data.domain.ScrollPosition.Direction;

import java.util.Map;

public record CursorPayload(
        Direction direction,
        Map<String, Object> keys
) { }
