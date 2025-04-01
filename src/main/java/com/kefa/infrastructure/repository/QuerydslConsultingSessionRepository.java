package com.kefa.infrastructure.repository;


import com.kefa.domain.entity.ConsultingSession;
import com.kefa.domain.entity.ConsultingSessionFeedback;

import java.util.List;
import java.util.Optional;

public interface QuerydslConsultingSessionRepository {

    Optional<ConsultingSession> findByIdWithCompanyAndDocumentsAndFeedback(Long id);
    List<ConsultingSessionFeedback> findFeedbacksBySessionId(Long id);

}
