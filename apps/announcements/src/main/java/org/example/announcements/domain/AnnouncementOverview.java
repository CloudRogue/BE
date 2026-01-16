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
        name = "announcement_overviews",
        uniqueConstraints = {
                @UniqueConstraint(
                        name = "uq_announcement_overview_announcement_id",
                        columnNames = {"announcement_id"}
                )
        }
)
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class AnnouncementOverview {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "overview_id")
    private Long id;

    @Column(name = "announcement_id", nullable = false)
    private Long announcementId;

    @Column(name = "content",nullable = false)
    private String content;

    @Column(name = "target",nullable = false)
    private String target;

    @Column(name = "apply_method", nullable = false)
    private String applyMethod;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at",nullable = false)
    private LocalDateTime updatedAt;


    //생성용 정적 메서드
    public static AnnouncementOverview create(
            Long announcementId,
            String content,
            String target,
            String applyMethod
    ){
        AnnouncementOverview overview = new AnnouncementOverview();
        overview.announcementId = announcementId;
        overview.content =  content;
        overview.target = target;
        overview.applyMethod = applyMethod;
        return overview;
    }


}
