package com.kefa.infrastructure.repository;

import com.kefa.domain.entity.ConsultingSession;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ConsultingSessionRepository extends JpaRepository<ConsultingSession, Long> {
    List<ConsultingSession> findAllByCompanyId(Long companyId);

    @Query("SELECT cs FROM ConsultingSession cs LEFT JOIN FETCH cs.participantAccountIds WHERE cs.id = :id")
    Optional<ConsultingSession> findByIdWithParticipants(@Param("id") Long id);

    @Query("SELECT cs FROM ConsultingSession cs LEFT JOIN FETCH cs.documents LEFT JOIN FETCH cs.company WHERE cs.id = :id")
    Optional<ConsultingSession> findByIdWithDocumentsAndCompany(Long id);

    @Query("SELECT cs FROM ConsultingSession cs LEFT JOIN FETCH cs.company LEFT JOIN FETCH cs.participantAccountIds WHERE cs.id = :id")
    Optional<ConsultingSession> findByIdWithCompanyAndParticipant(Long consultingSessionId);
}
