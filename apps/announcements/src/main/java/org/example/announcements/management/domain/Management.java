package org.example.announcements.management.domain;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.example.announcements.domain.Announcement;
import org.springframework.util.Assert;

import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Table(name = "managements")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Management {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "announcement_id", nullable = false)
    private Announcement announcement;

    // step
    @Column(name = "step_order",nullable = false)
    private int stepOrder;

    @Column(name = "step_name", nullable = false)
    private String stepName;

    @Column(name = "is_current_step", nullable = false)
    private boolean isCurrentStep;

    @Column(columnDefinition = "TEXT")
    private String content;

    // section
    @Enumerated(EnumType.STRING)
    @Column(name = "section_title", nullable = false)
    private ManageSectionType sectionTitle;

    @OneToMany(mappedBy = "management", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ManagementDocument> documents = new ArrayList<>();

    @Builder
    public Management(Announcement announcement, int stepOrder, String stepName, boolean isCurrentStep, String content, ManageSectionType sectionTitle) {
        Assert.notNull(announcement, "공고 정보는 필수입니다.");
        Assert.hasText(stepName, "단계 이름은 필수입니다.");
        this.announcement = announcement;
        this.stepOrder = stepOrder;
        this.stepName = stepName;
        this.isCurrentStep = isCurrentStep;
        this.content = content;
        this.sectionTitle = sectionTitle;
    }

    public void addDocument(ManagementDocument document) {
        this.documents.add(document);

        if(document.getManagement() != this) {
            document.assignManagement(this);
        }
    }
}