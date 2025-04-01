package com.kefa.infrastructure.repository;

import com.kefa.domain.entity.ConsultingSessionFeedback;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ConsultingSessionFeedbackRepository extends JpaRepository<ConsultingSessionFeedback, Long> {
}
