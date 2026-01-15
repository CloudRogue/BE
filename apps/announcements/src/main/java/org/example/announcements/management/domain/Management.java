package org.example.announcements.management.domain;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.example.announcements.domain.Announcement;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "managements")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Management {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "announcement_id", nullable = false)
    private Announcement announcementId;

    @OneToMany(mappedBy = "management", cascade = CascadeType.ALL)
    private List<ManageStepDetail> details = new ArrayList<>();
}

/*
applyingCount, documentWaitingCount, finalWatingCount 쿼리로 처리
dDay 계산해서 반환

 */