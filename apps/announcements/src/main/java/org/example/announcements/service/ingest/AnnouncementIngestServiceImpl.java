package org.example.announcements.service.ingest;

import org.example.announcements.dto.ingest.AnnouncementIngestItem;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class AnnouncementIngestServiceImpl implements AnnouncementIngestService {
    @Override
    public IngestResult ingest(List<AnnouncementIngestItem> items) {
        return null;
    }
}
