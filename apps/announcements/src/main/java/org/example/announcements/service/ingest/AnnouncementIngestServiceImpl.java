package org.example.announcements.service.ingest;

import lombok.RequiredArgsConstructor;
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

        int created = 0; // 신규 insert 건수
        int skipped = 0; // 중복 및 비정상으로 스킵건수

        // 파싱서버가 보내준 공고 목록을 한 건씩 처리
        for (AnnouncementIngestItem item : items) {
            if (item == null) {
                skipped++;
                continue;
            }

            //    같은 공고를 파싱서버가 다시 보내도 유니크 키 존재시 인서트하지 않음
            boolean exists = announcementRepository
                    .findBySourceAndExternalKey(item.source(), item.externalKey())
                    .isPresent();

            if (exists) {
                // 이미 있으면 중복이므로 저장 생략
                skipped++;
                continue;
            }

            // 신규 insert
            announcementRepository.save(AnnouncementIngestMapper.toEntity(item));
            created++;
        }
            return new IngestResult(
                    items.size(),
                    created,
                    0,// MVP 단에서는 업데이트가 없으므로 일단 0으로 대체
                    skipped
            );
    }


}
