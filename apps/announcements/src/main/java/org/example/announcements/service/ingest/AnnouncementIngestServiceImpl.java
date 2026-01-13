package org.example.announcements.service.ingest;

import lombok.RequiredArgsConstructor;
import org.example.announcements.domain.Announcement;
import org.example.announcements.dto.ingest.AnnouncementIngestItem;
import org.example.announcements.repository.AnnouncementRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

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
        // 배치 내부 중복 제거용
        Set<String> seenInBatch = new HashSet<>();

        // 파싱서버가 보내준 공고 목록을 한 건씩 처리
        for (AnnouncementIngestItem item : items) {
            if (item == null) { skipped++; continue; }

            // externalKey 방어 + trim
            String externalKey = item.externalKey();
            if (externalKey == null || externalKey.isBlank()) {
                skipped++;
                continue;
            }
            externalKey = externalKey.trim();

            // 배치 내부 중복 컷 (source + externalKey)
            String batchKey = item.source().name() + "::" + externalKey;
            if (!seenInBatch.add(batchKey)) { // add 실패 => 이미 존재
                skipped++;
                continue;
            }

            // DB 중복 컷
            boolean exists = announcementRepository
                    .findBySourceAndExternalKey(item.source(), externalKey)
                    .isPresent();

            if (exists) {
                skipped++;
                continue;
            }


            toSave.add(AnnouncementIngestMapper.toEntity(item));

        }
        if (!toSave.isEmpty()) announcementRepository.saveAll(toSave);

        int created = toSave.size();
        return new IngestResult(items.size(), created, 0, skipped);
    }


}
