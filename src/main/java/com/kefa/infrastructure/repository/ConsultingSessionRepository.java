package com.kefa.infrastructure.repository;

import com.kefa.domain.entity.ConsultingSession;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ConsultingSessionRepository extends JpaRepository<ConsultingSession, Long> {
    List<ConsultingSession> findAllByCompanyId(Long companyId);
}
