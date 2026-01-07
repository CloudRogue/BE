package org.example.core.domain;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Getter
@Table(name = "todo")
@NoArgsConstructor
@Entity
public class toDo {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // api-sec 상으론 null 허용임
    @JoinColumn(name = "announcementId", nullable = false)
    private Long announcementId;

    @Column(name = "title", nullable = false)
    private String title;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private toDoStatus status;

    @Column(name = "dueDate")
    private LocalDateTime dueDate;

    @Column(name = "orderIndex", nullable = false)
    private Long orderIndex;

    public toDo(Long announcementId, String title, toDoStatus status, LocalDateTime dueDate, Long orderIndex) {
        this.announcementId = announcementId;
        this.title = title;
        this.status = status;
        this.dueDate = dueDate;
        this.orderIndex = orderIndex;
    }


}
