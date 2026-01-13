package org.example.announcements.service.ingest;

import lombok.RequiredArgsConstructor;
import org.example.announcements.domain.Announcement;
import org.example.announcements.dto.ingest.AnnouncementIngestItem;
import org.example.announcements.repository.AnnouncementRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class AnnouncementIngestServiceImpl implements AnnouncementIngestService {

    private final AnnouncementRepository announcementRepository;

    @Transactional
    @Override
    public IngestResult ingest(List<AnnouncementIngestItem> items) {
        if (items == null || items.isEmpty()) {
            return IngestResult.empty();
        }


        int skipped = 0; // 중복 및 비정상으로 스킵건수

        List<Announcement> toSave = new java.util.ArrayList<>();

        // 파싱서버가 보내준 공고 목록을 한 건씩 처리
        for (AnnouncementIngestItem item : items) {
            if (item == null) { skipped++; continue; }

            boolean exists = announcementRepository
                    .findBySourceAndExternalKey(item.source(), item.externalKey().trim())
                    .isPresent();

            if (exists) { skipped++; continue; }

            toSave.add(AnnouncementIngestMapper.toEntity(item));

        }
        if (!toSave.isEmpty()) announcementRepository.saveAll(toSave);

        int created = toSave.size();
        return new IngestResult(items.size(), created, 0, skipped);
    }


}
