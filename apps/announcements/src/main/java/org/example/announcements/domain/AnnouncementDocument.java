package org.example.announcements.domain;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(
        name = "announcement_documents",
        indexes = {
                @Index(name = "idx_ann_doc_announcement_phase", columnList = "announcement_id,phase")
        }
)
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class AnnouncementDocument {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "announcement_id", nullable = false)
    private Announcement announcement;

    //문서가 어디 단계에 속하냐
    @Enumerated(EnumType.STRING)
    @Column(name = "phase", nullable = false)
    private AnnouncementDocumentPhase phase;

    //문서범위(공통이냐 대상자전용이냐)
    @Enumerated(EnumType.STRING)
    @Column(name = "scope", nullable = false, length = 20)
    private AnnouncementDocumentScope scope;

    //서류명
    @Column(name = "name", nullable = false)
    private String name;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;


    public static AnnouncementDocument create(
            Announcement announcement,
            AnnouncementDocumentPhase phase,
            AnnouncementDocumentScope scope,
            String name
    ) {
        AnnouncementDocument d = new AnnouncementDocument();
        d.announcement = announcement;
        d.phase = phase;
        d.scope = scope;
        d.name = name;
        return d;
    }
}
