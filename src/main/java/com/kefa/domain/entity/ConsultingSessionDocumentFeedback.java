package com.kefa.domain.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;


@Entity
@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Table(name = "consulting_session_document_feedback")
public class ConsultingSessionDocumentFeedback {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "consulting_session_document_id", nullable = false)
    private ConsultingSessionDocument consultingSessionDocument;

    @Column(nullable = false)
    private Long accountId;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private String content;

}
