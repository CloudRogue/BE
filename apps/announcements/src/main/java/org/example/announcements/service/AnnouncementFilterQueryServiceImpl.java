package org.example.announcements.service;

import lombok.RequiredArgsConstructor;
import org.example.announcements.domain.AnnouncementSource;
import org.example.announcements.dto.filters.HousingTypesFilterResponse;
import org.example.announcements.dto.filters.PublishersFilterResponse;
import org.example.announcements.repository.AnnouncementRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Objects;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class AnnouncementFilterQueryServiceImpl implements AnnouncementFilterQueryService {

    private final AnnouncementRepository announcementRepository;

    @Override
    public PublishersFilterResponse getPublishers() {
        List<AnnouncementSource> sources = announcementRepository.findDistinctSources();

        // null 방어및 LH SH 만 내려가도록
        List<String> publishers = sources.stream()
                .map(this::mapSourceToPublisherEnum)
                .filter(Objects::nonNull)
                .distinct()
                .sorted()
                .toList();


        return PublishersFilterResponse.of(publishers);
    }

    @Override
    public HousingTypesFilterResponse getHousingTypes() {
        List<String> housingTypes = announcementRepository.findDistinctHousingTypes();
        return HousingTypesFilterResponse.of(housingTypes);
    }


    private String mapSourceToPublisherEnum(AnnouncementSource source) {
        if (source == null) return null;
        return switch (source) {
            case MYHOME -> "LH";
            case SH_RSS -> "SH";
        };
    }
}
