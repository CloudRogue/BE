package org.example.announcements.management.domain;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.util.Assert;

@Entity
@Getter
@Table(name = "management_documents")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ManagementDocument {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "management_id", nullable = false)
    private Management management;

    // document
    @Column(name = "document_id", length = 36)
    private String documentId;  // UUID 사용

    @Column(name = "document_name", nullable = false)
    private String documentName;

    @Enumerated(EnumType.STRING)
    @Column(name = "document_type", nullable = false)
    private ManagementDocType docType;

    @Builder
    public ManagementDocument(Management management, String documentId, String documentName, ManagementDocType docType) {
        Assert.notNull(management, "부모 단계 정보는 필수입니다.");
        Assert.hasText(documentName, "문서 이름은 필수입니다.");
        this.management = management;
        this.documentId = documentId;
        this.documentName = documentName;
        this.docType = docType;
    }

    public void assignManagement(Management management) {
        if(this.management != null) {
            this.management.getDocuments().remove(this);
        }
        this.management = management;

        if (!management.getDocuments().contains(this)) {
            management.getDocuments().add(this);
        }
    }
}
