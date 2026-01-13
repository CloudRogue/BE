package org.example.announcements.controller.internal;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.example.announcements.dto.ingest.AnnouncementIngestRequest;
import org.example.announcements.service.ingest.AnnouncementIngestService;
import org.example.announcements.service.ingest.IngestResult;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

// 파싱서버가 신규 공고가 있을 때 HTTPS로 호출하는 내부 API임
@RestController
@RequiredArgsConstructor
@RequestMapping("/internal/announcements")
public class AnnouncementIngestController {

    private final AnnouncementIngestService ingestService;

    //파싱서버에서 메인서버 공고 적재
    @PostMapping("/ingest")
    public ResponseEntity<IngestResult> ingest(@RequestBody @Valid AnnouncementIngestRequest request) {
        IngestResult result = ingestService.ingest(request.items());
        return ResponseEntity.ok(result);
    }

}
