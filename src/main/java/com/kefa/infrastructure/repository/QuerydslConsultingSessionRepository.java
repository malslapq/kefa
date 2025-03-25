package com.kefa.infrastructure.repository;

import com.kefa.api.dto.consulting.response.ConsultingSessionDetailResponse;

import java.util.Optional;

public interface QuerydslConsultingSessionRepository {

    Optional<ConsultingSessionDetailResponse> findByIdWithDocumentsAndCompanyAndFeedback(Long id);

}
