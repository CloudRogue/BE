package org.example.announcements.service.ingest;

import org.example.announcements.dto.ingest.AnnouncementIngestItem;

import java.util.List;

public interface AnnouncementIngestService {
    //파싱서버에서 넘어온 공고 목록을 받아 메인서버 DB상태 최신화하는 메서드
    IngestResult ingest(String category, List<AnnouncementIngestItem> items);
}
