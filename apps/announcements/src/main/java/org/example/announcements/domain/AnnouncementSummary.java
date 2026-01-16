package org.example.announcements.domain;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(
        name = "announcement_summaries",
        uniqueConstraints = {
                @UniqueConstraint(
                        name = "uq_announcement_summary_announcement_id",
                        columnNames = {"announcement_id"}
                )
        }
)
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class AnnouncementSummary {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "summary_id")
    private Long id;

    @Column(name = "announcement_id", nullable = false)
    private Long announcementId;

    @Column(name = "summary")
    private String summary;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    // 요약 수정 메서드
    public static AnnouncementSummary create(Long announcementId, String summary) {
        AnnouncementSummary announcementSummary = new AnnouncementSummary();
        announcementSummary.announcementId = announcementId;
        announcementSummary.summary = trimToNull(summary);
        return announcementSummary;
    }

    public void updateSummary(String summary) {
        this.summary = trimToNull(summary);
    }

    //문자열 정리 메서드
    private static String trimToNull(String v){
        if(v == null) return null;
        String t = v.trim();
        return t.isEmpty() ? null : t;
    }

}
