package com.kefa.domain.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.SQLRestriction;


@Entity
@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@SQLDelete(sql = "UPDATE consulting_session_document_feedback SET deleted = true, deleted_at = NOW() WHERE id = ?")
@SQLRestriction("deleted = false")
@Table(name = "consulting_session_document_feedback")
public class DocumentFeedback extends BaseEntity {

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

    public void updateContent(String content) {
        this.content = content;
    }
}
