package com.kefa.infrastructure.repository;

import com.kefa.api.dto.consulting.response.ConsultingSessionDetailResponse;
import com.kefa.api.dto.consulting.response.ConsultingSessionDocumentDto;
import com.kefa.api.dto.consulting.response.FeedbackResponse;
import com.kefa.domain.type.FeedbackType;
import com.querydsl.core.types.Projections;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.Optional;

import static com.kefa.domain.entity.QCompany.company;
import static com.kefa.domain.entity.QConsultingSession.consultingSession;
import static com.kefa.domain.entity.QConsultingSessionDocument.consultingSessionDocument;
import static com.kefa.domain.entity.QFeedback.feedback;


@Repository
@RequiredArgsConstructor
public class QuerydslConsultingSessionRepositoryImpl implements QuerydslConsultingSessionRepository {

    private final JPAQueryFactory queryFactory;

    @Override
    public Optional<ConsultingSessionDetailResponse> findByIdWithDocumentsAndCompanyAndFeedback(Long id) {
        return Optional.ofNullable(queryFactory
            .select(Projections.constructor(ConsultingSessionDetailResponse.class,
                consultingSession.id,
                consultingSession.company.id,
                consultingSession.status,
                Projections.list(Projections.constructor(ConsultingSessionDocumentDto.class,
                    consultingSessionDocument.id,
                    consultingSessionDocument.fileUrl,
                    consultingSessionDocument.name
                )),
                Projections.list(Projections.constructor(FeedbackResponse.class,
                    feedback.id,
                    feedback.targetId,
                    feedback.accountId,
                    feedback.parentId,
                    feedback.content,
                    feedback.feedbackType,
                    feedback.createdAt
                ))
            ))
            .from(consultingSession)
            .join(consultingSession.company, company)
            .leftJoin(consultingSession.documents, consultingSessionDocument)
            .leftJoin(feedback)
            .on(feedback.targetId.eq(consultingSession.id)
                .and(feedback.feedbackType.eq(FeedbackType.SESSION)))
            .where(consultingSession.id.eq(id))
            .fetchOne());
    }

}
