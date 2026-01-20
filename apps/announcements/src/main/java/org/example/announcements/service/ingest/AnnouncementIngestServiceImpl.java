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
import org.springframework.transaction.support.TransactionSynchronization;
import org.springframework.transaction.support.TransactionSynchronizationManager;

import java.util.ArrayList;
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
    public IngestResult ingest(String requestCategory, List<AnnouncementIngestItem> items) {
        if (items == null || items.isEmpty()) {
            return IngestResult.empty();
        }


        int skipped = 0; // 중복 및 비정상으로 스킵건수

        List<Announcement> toSave = new ArrayList<>();
        // 배치 내부 중복 제거용
        Set<String> seenInBatch = new HashSet<>();

        Set<String> myhomeStdIdsToPublish = new HashSet<>();
        Set<String> shStdIdsToPublish = new HashSet<>();


        // 파싱서버가 보내준 공고 목록을 한 건씩 처리
        for (AnnouncementIngestItem item : items) {
            if (item == null) { skipped++; continue; }

            AnnouncementSource source = item.source();
            if (source == null) {
                skipped++;
                continue;
            }

            // externalKey 방어 + trim
            String externalKey = item.externalKey();
            if (externalKey == null || externalKey.isBlank()) {
                skipped++;
                continue;
            }
            externalKey = externalKey.trim();


            // 배치 내부 중복 컷 (source + externalKey)
            String batchKey = item.source().name() + "::" + externalKey;
            if (!seenInBatch.add(batchKey)) {
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

            // 7) Redis에 넣을 stdId도 저장 대상에 대해서만 생성해서 모아둔다.
            String effectiveCategory = resolveCategory(source, requestCategory);

            String stdId = SeenStdIdMapper.toStdId(source, effectiveCategory, externalKey);
            if (stdId != null && !stdId.isBlank()) {
                stdId = stdId.trim();

                // source별로 버킷(set key)이 다르므로 분리해서 모아둔다.
                if (source == AnnouncementSource.MYHOME) {
                    myhomeStdIdsToPublish.add(stdId);
                } else if (source == AnnouncementSource.SH_RSS) {
                    shStdIdsToPublish.add(stdId);
                }
            }

        }
        // 저장할 것이 없으면  작업 없이 결과 반환
        if (toSave.isEmpty()) {
            return new IngestResult(items.size(), 0, 0, skipped);
        }


        announcementRepository.saveAll(toSave);

        // 디비 커밋이후에 레디스 등록
        registerAfterCommitPublish(requestCategory, myhomeStdIdsToPublish, shStdIdsToPublish);


        int created = toSave.size();
        return new IngestResult(items.size(), created, 0, skipped);
    }

    // 소스가 뭐냐에따라 뭘사용할지 결정
    private String resolveCategory(AnnouncementSource source, String requestCategory) {
        if (source == AnnouncementSource.SH_RSS) {
            return shFixedCategory;
        }
        return requestCategory;
    }


    // 트랜잭션 커밋 성공이후에만 등록하기
    private void registerAfterCommitPublish(
            String requestCategory,
            Set<String> myhomeStdIdsToPublish,
            Set<String> shStdIdsToPublish
    ) {

        // 트랜잭션이 실제로 활성 상태인 경우에만 동기화 훅 등록
        if (!TransactionSynchronizationManager.isSynchronizationActive()) {
            return;
        }

        TransactionSynchronizationManager.registerSynchronization(new TransactionSynchronization() {

            //DB 커밋이 성공한 이후에만 사용
            @Override
            public void afterCommit() {

                // 1) MYHOME seen 기록
                if (myhomeStdIdsToPublish != null && !myhomeStdIdsToPublish.isEmpty()) {
                    seenStdIdWriterPort.addSeenStdIds(
                            "myhome",
                            requestCategory,
                            ingestScope,
                            myhomeStdIdsToPublish
                    );
                }

                // 2) SH_RSS seen 기록
                if (shStdIdsToPublish != null && !shStdIdsToPublish.isEmpty()) {
                    seenStdIdWriterPort.addSeenStdIds(
                            "sh",
                            shFixedCategory,
                            ingestScope,
                            shStdIdsToPublish
                    );
                }
            }
        });
    }


}
