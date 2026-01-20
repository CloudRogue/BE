package org.example.announcements.redis;

import org.example.announcements.domain.AnnouncementSource;
import org.example.announcements.dto.ingest.AnnouncementIngestItem;
import org.example.announcements.repository.AnnouncementRepository;
import org.example.announcements.service.ingest.AnnouncementIngestService;
import org.example.announcements.service.ingest.IngestResult;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.test.context.ActiveProfiles;


import java.time.LocalDate;
import java.util.List;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@ActiveProfiles("test")
@Tag("it")
class AnnouncementIngestRedisWriteIT {

    @Autowired AnnouncementIngestService ingestService;
    @Autowired StringRedisTemplate stringRedisTemplate;
    @Autowired AnnouncementRepository announcementRepository;

    @BeforeEach
    void clean() {
        // DB 비우기 (중복 스킵 방지)
        announcementRepository.deleteAll();

        stringRedisTemplate.delete("seoulhousing:local:seen:myhome:rsdt:seoul");
        stringRedisTemplate.delete("seoulhousing:local:seen:sh:rental:seoul");
    }

    @Test
    void ingest_should_write_seenStdId_sets_after_commit() {
        // given
        AnnouncementIngestItem myhome = new AnnouncementIngestItem(
                AnnouncementSource.MYHOME,
                "1345:1",
                "테스트-마이홈",
                "LH",
                "아파트",
                "공공임대",
                "서울",
                LocalDate.now().minusDays(1),
                LocalDate.now().plusDays(10),
                null,
                null,
                "https://example.com/lh/1345",
                null, null, null, null, null,
                "서울시 어딘가",
                "법정동"
        );

        AnnouncementIngestItem sh = new AnnouncementIngestItem(
                AnnouncementSource.SH_RSS,
                "298388",
                "테스트-SH",
                "SH",
                null,
                null,
                "서울",
                LocalDate.now().minusDays(1),
                LocalDate.now().plusDays(5),
                null,
                null,
                "https://example.com/sh/298388",
                null, null, null, null, null,
                null,
                null
        );

        // when: 서비스 내부 @Transactional이 커밋되면서 afterCommit가 실행됨
        IngestResult result = ingestService.ingest("rsdt", List.of(myhome, sh));

        // then: DB 저장 확인
        assertThat(result.created()).isEqualTo(2);
        assertThat(announcementRepository.count()).isEqualTo(2);

        // then: Redis 저장 확인
        // application.yml + .env 기준:
        // env=local, scope=seoul, sh-category=rental
        String myhomeKey = "seoulhousing:local:seen:myhome:rsdt:seoul";
        String shKey     = "seoulhousing:local:seen:sh:rental:seoul";

        Set<String> myhomeMembers = stringRedisTemplate.opsForSet().members(myhomeKey);
        Set<String> shMembers = stringRedisTemplate.opsForSet().members(shKey);

        assertThat(myhomeMembers).isNotNull();
        assertThat(shMembers).isNotNull();

        // MYHOME stdId 규칙: myhome:{category}:{externalKey}
        assertThat(myhomeMembers).contains("myhome:rsdt:1345:1");

        // SH stdId 규칙: sh:rss:{seq}
        assertThat(shMembers).contains("sh:rss:298388");
    }
}