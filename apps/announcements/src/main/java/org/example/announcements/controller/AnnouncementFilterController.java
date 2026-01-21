package org.example.announcements.controller;

import lombok.RequiredArgsConstructor;
import org.example.announcements.dto.filters.HousingTypesFilterResponse;
import org.example.announcements.dto.filters.PublishersFilterResponse;
import org.example.announcements.service.AnnouncementFilterQueryService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/announcements/filters")
public class AnnouncementFilterController {

    private final AnnouncementFilterQueryService   filterQueryService;

    //공고 필터 - 발행기관 목록 조회
    @GetMapping("/publishers")
    public ResponseEntity<PublishersFilterResponse> getPublishers() {
        return ResponseEntity.ok(filterQueryService.getPublishers());
    }

    //공고 필터 - 주택유형 목록 조회
    @GetMapping("/housing-types")
    public ResponseEntity<HousingTypesFilterResponse> getHousingTypes() {
        return ResponseEntity.ok(filterQueryService.getHousingTypes());
    }
}
