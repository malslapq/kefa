package com.kefa.infrastructure.repository;

import com.kefa.domain.entity.ConsultingSessionDocument;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ConsultingDocumentRepository extends JpaRepository<ConsultingSessionDocument, Long> {

    Page<ConsultingSessionDocument> findByConsultingSessionId(Long consultingSessionId, Pageable pageable);

}
