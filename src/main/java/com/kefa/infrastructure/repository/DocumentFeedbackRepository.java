package com.kefa.infrastructure.repository;

import com.kefa.domain.entity.DocumentFeedback;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface DocumentFeedbackRepository extends JpaRepository<DocumentFeedback, Long> {
    List<DocumentFeedback> findAllByConsultingSessionDocumentId(Long id);

    @Query("SELECT df FROM DocumentFeedback df LEFT JOIN FETCH df.consultingSessionDocument WHERE df.id = :id")
    Optional<DocumentFeedback> findByIdWithDocument(Long id);
}
