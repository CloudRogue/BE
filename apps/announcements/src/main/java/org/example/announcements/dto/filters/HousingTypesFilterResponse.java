package org.example.announcements.dto.filters;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.List;

@Getter
@RequiredArgsConstructor
public class HousingTypesFilterResponse {

    private final List<String> housingTypes;

    public static HousingTypesFilterResponse of(List<String> housingTypes) {
        return new HousingTypesFilterResponse(housingTypes);
    }
}