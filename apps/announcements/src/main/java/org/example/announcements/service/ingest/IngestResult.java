package org.example.announcements.service.ingest;

// 파싱서버 ingest 요청 처리결과 요약
public record IngestResult(
        int received, // 요청으로 들어온 공고 수
        int created,  // 신규로 insert된 수
        int updated,  // 기존 공고 중 변경되어 update된 수
        int skipped   // 동일해서 DB 반영을 생략한 수
) {
    public static IngestResult empty() {
        return new IngestResult(0, 0, 0, 0);
    }
}
