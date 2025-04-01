package com.kefa.infrastructure.repository;

import com.kefa.domain.entity.ConsultingSession;
import com.kefa.domain.entity.ConsultingSessionFeedback;
import com.kefa.domain.entity.QConsultingSessionDocumentFeedback;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

import static com.kefa.domain.entity.QCompany.company;
import static com.kefa.domain.entity.QConsultingSession.consultingSession;
import static com.kefa.domain.entity.QConsultingSessionDocument.consultingSessionDocument;
import static com.kefa.domain.entity.QConsultingSessionFeedback.consultingSessionFeedback;


@Repository
@RequiredArgsConstructor
public class QuerydslConsultingSessionRepositoryImpl implements QuerydslConsultingSessionRepository {

    private final JPAQueryFactory queryFactory;

    @Override
    public Optional<ConsultingSession> findByIdWithCompanyAndDocumentsAndFeedback(Long id) {
        ConsultingSession session = queryFactory
            .selectFrom(consultingSession)
            .join(consultingSession.company, company).fetchJoin()
            .leftJoin(consultingSession.documents, consultingSessionDocument).fetchJoin()
            .where(consultingSession.id.eq(id))
            .distinct()
            .fetchOne();

        return Optional.ofNullable(session);
    }

    @Override
    public List<ConsultingSessionFeedback> findFeedbacksBySessionId(Long id) {
        return queryFactory
            .selectFrom(consultingSessionFeedback)
            .where(consultingSessionFeedback.consultingSession.id.eq(id))
            .fetch();
    }
}
