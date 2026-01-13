package org.example.announcements.domain;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Entity
@Table( name = "announcement_scraps",
        uniqueConstraints = { // 중복 찜 방지
                @UniqueConstraint(
                        name = "uq_scrap_user_announcement", // 제약 이름
                        columnNames = {"user_id", "announcement_id"} // 유저+공고 유니크
                )
        })
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class AnnouncementScrap {

    @Id // PK
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "scrap_id")
    private Long id;

    @Column(name = "user_id", nullable = false)
    private String userId; // 유저 ID (ULID)

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "announcement_id", nullable = false)
    private Announcement announcement; // 대상 공고

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;
}
