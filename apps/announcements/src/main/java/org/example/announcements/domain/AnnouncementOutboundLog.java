package org.example.announcements.domain;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "announcement_outbound_logs")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class AnnouncementOutboundLog {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "outbound_id")
    private Long id;

    @Column(name = "user_id", nullable = false)
    private String userId; // 유저 ID (ULID)


    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "announcement_id", nullable = false)
    private Announcement announcement; // 대상 공고

    @Column(name = "destination_url") // 이게 공고url이 변경될수도있으므로 그냥 기록으로 설정
    private String destinationUrl; // 이동 대상 URL

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;
}
