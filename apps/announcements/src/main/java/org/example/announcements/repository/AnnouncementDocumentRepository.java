package org.example.announcements.repository;

import org.example.announcements.domain.AnnouncementDocument;
import org.example.announcements.domain.AnnouncementDocumentPhase;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AnnouncementDocumentRepository extends JpaRepository<AnnouncementDocument, Long> {

    //통합저장에서 덮어쓰기 금지 판단용
    boolean existsByAnnouncement_IdAndPhase(Long announcementId, AnnouncementDocumentPhase phase);

    //단계별 문서 조회용
    List<AnnouncementDocument> findAllByAnnouncement_IdAndPhaseOrderByIdAsc(
            Long announcementId,
            AnnouncementDocumentPhase phase
    );

}
