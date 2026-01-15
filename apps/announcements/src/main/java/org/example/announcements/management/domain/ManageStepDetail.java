package org.example.announcements.management.domain;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ManageStepDetail {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "management_id", nullable = false)
    private Management management;

    // step
    @Column(name = "step_order",nullable = false)
    private int stepOrder;

    @Column(name = "step_name", nullable = false)
    private String stepName;

    @Column(name = "is_current_step", nullable = false)
    private boolean isCurrentStep;

    // section
    @Enumerated(EnumType.STRING)
    @Column(name = "sectoin_title", nullable = false)
    private ManageSection sectionTitle;

    // document
    @Column(name = "document_id", nullable = false)
    private Long documentId;

    @Column(name = "document_name", nullable = false)
    private String documentName;

    @Enumerated(EnumType.STRING)
    @Column(name = "document_type", nullable = false)
    private ManagementDocType documentType;
}