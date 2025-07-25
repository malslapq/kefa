package com.kefa.domain.entity;

import com.kefa.common.type.FeedbackType;
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
@SQLDelete(sql = "UPDATE consulting_session_feedback SET deleted = true, deleted_at = NOW() WHERE id = ?")
@SQLRestriction("deleted = false")
@Table(name = "consulting_session_feedback")
public class ConsultingSessionFeedback extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;


    @ManyToOne
    @JoinColumn(name = "consulting_session_id", nullable = false)
    private ConsultingSession consultingSession;

    @Column(nullable = false)
    private Long accountId;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private String content;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private FeedbackType feedbackType;


    public void updateContentAndType( String content, FeedbackType feedbackType) {
        this.content = content;
        this.feedbackType = feedbackType;
    }

}
