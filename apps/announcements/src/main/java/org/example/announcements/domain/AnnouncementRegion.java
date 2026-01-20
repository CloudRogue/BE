package org.example.announcements.domain;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(
        name = "announcement_regions",
        uniqueConstraints = @UniqueConstraint(
                name = "uq_announcement_region",
                columnNames = {"announcement_id", "region_name"}
        )
)
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class AnnouncementRegion {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "region_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "announcement_id", nullable = false)
    private Announcement announcement;

    @Column(name = "region_name", nullable = false, length = 100)
    private String regionName;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    public static AnnouncementRegion create(Announcement announcement, String regionName) {
        AnnouncementRegion r = new AnnouncementRegion();
        r.announcement = announcement;
        r.regionName = regionName;
        return r;
    }
}
