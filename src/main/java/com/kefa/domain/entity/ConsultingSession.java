package com.kefa.domain.entity;

import com.kefa.domain.type.ConsultingStatus;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.BatchSize;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Entity
@Table(name = "consulting_session")
public class ConsultingSession extends BaseEntity{

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "company_id", nullable = false)
    private Company company;

    @Builder.Default
    @OneToMany(fetch = FetchType.LAZY, mappedBy = "consultingSession")
    private List<ConsultingSessionDocument> documents = new ArrayList<>();

    @Builder.Default
    @OneToMany(fetch = FetchType.LAZY, mappedBy = "consultingSession")
    private List<ConsultingSessionFeedback> feedbacks = new ArrayList<>();

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ConsultingStatus status;

    @Builder.Default
    @ElementCollection
    @CollectionTable(name = "consulting_session_participants", joinColumns = @JoinColumn(name = "consulting_session_id"))
    @Column(name = "account_id")
    private Set<Long> participantAccountIds = new HashSet<>();

    public void addParticipantAccountId(Long accountId) {
        this.participantAccountIds.add(accountId);
    }

    public void addDocuments(List<ConsultingSessionDocument> documents) {
        this.documents.addAll(documents);
    }

    public void addFeedback(ConsultingSessionFeedback feedback) {
        this.feedbacks.add(feedback);
    }

}
