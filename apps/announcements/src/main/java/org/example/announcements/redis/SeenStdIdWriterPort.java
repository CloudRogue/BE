package org.example.announcements.redis;

import java.util.Collection;

public interface SeenStdIdWriterPort {

    void addSeenStdIds(
            String source,
            String category,
            String scope,
            Collection<String> stdIds // 추가할 목록
    );
}
