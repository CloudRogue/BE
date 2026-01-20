package org.example.announcements.dto.filters;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.List;

@Getter
@RequiredArgsConstructor
public class PublishersFilterResponse {

    private final List<String> publishers;

    public static PublishersFilterResponse of(List<String> publishers) {
        return new PublishersFilterResponse(publishers);
    }
}
