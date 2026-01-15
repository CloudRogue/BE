package org.example.announcements.service.ingest;

import lombok.RequiredArgsConstructor;
import org.example.announcements.domain.Announcement;
import org.example.announcements.domain.AnnouncementSource;
import org.example.announcements.dto.ingest.AnnouncementIngestItem;
import org.example.announcements.redis.SeenStdIdMapper;
import org.example.announcements.redis.SeenStdIdWriterPort;
import org.example.announcements.repository.AnnouncementRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionSynchronizationAdapter;
import org.springframework.transaction.support.TransactionSynchronizationManager;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class AnnouncementIngestServiceImpl implements AnnouncementIngestService {

    private final AnnouncementRepository announcementRepository;
    private final SeenStdIdWriterPort seenStdIdWriterPort;

    @Value("${announcements.ingest.scope}")
    private String ingestScope;

    @Value("${announcements.ingest.sh-category}")
    private String shFixedCategory;

    @Transactional
    @Override
    public IngestResult ingest(String category, List<AnnouncementIngestItem> items) {
        if (items == null || items.isEmpty()) {
            return IngestResult.empty();
        }


        int skipped = 0; // 중복 및 비정상으로 스킵건수

        List<Announcement> toSave = new java.util.ArrayList<>();
        // 배치 내부 중복 제거용
        Set<String> seenInBatch = new HashSet<>();

        // 레디스에 넣을 stdId 모음
        Set<String> stdIdsToPublish = new HashSet<>();

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

            if (item.source() == null) { skipped++; continue; }

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

            //레디스에 넣을 stdId 멤버 생성
            String effectiveCategory = resolveCategory(item.source(), category);

        }
        if (!toSave.isEmpty()) announcementRepository.saveAll(toSave);

        int created = toSave.size();
        return new IngestResult(items.size(), created, 0, skipped);
    }

    //소스에 따라 카테고리 확정
    private String resolveCategory(AnnouncementSource source, String requestCategory) {

        if (source == AnnouncementSource.SH_RSS) {
            return shFixedCategory;
        }

        // 마이홈은 요청에서 받아온 카테고리를 사용
        return requestCategory;
    }

    // 커밋 이후 Redis 발행 등록
    private void registerAfterCommitPublish(String requestCategory, List<AnnouncementIngestItem> items, Set<String> stdIdsToPublish) {

        //소스별로 버킷 키가 달라서 2번 나눠서 해야함
        TransactionSynchronizationManager.registerSynchronization(new TransactionSynchronizationAdapter() {

            @Override
            public void afterCommit() {
                //마이홈이랑 sh 발행
                Set<String> myhomeStdIds = new HashSet<>();
                Set<String> shStdIds = new HashSet<>();

                //아이템들 기반으로 소스별로 다시분리
                for (AnnouncementIngestItem item : items) {
                    if (item == null || item.source() == null) continue;

                    String externalKey = item.externalKey();
                    if (externalKey == null || externalKey.isBlank()) continue;
                    externalKey = externalKey.trim();

                    if (item.source() == AnnouncementSource.MYHOME) {
                        String stdId = SeenStdIdMapper.toStdId(AnnouncementSource.MYHOME, requestCategory, externalKey);
                        if (stdId != null && !stdId.isBlank()) myhomeStdIds.add(stdId.trim());
                    }

                    if (item.source() == AnnouncementSource.SH_RSS) {
                        String stdId = SeenStdIdMapper.toStdId(AnnouncementSource.SH_RSS, shFixedCategory, externalKey);
                        if (stdId != null && !stdId.isBlank()) shStdIds.add(stdId.trim());
                    }
                }

                //레디스에 적기
                if (!myhomeStdIds.isEmpty()) {
                    seenStdIdWriterPort.addSeenStdIds(
                            "myhome",
                            requestCategory,
                            ingestScope,
                            myhomeStdIds
                    );
                }

                if (!shStdIds.isEmpty()) {
                    seenStdIdWriterPort.addSeenStdIds(
                            "sh",
                            shFixedCategory,
                            ingestScope,
                            shStdIds
                    );
                }
            }




        });

    }


}
