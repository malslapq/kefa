package com.kefa.application.usecase;

import com.kefa.api.dto.consulting.command.AddConsultingFeedbackCommand;
import com.kefa.api.dto.consulting.request.UpdateConsultingFeedbackRequest;
import com.kefa.api.dto.consulting.response.ConsultingSessionDetail;
import com.kefa.api.dto.consulting.response.ConsultingSessionFeedbackDto;
import com.kefa.api.dto.consulting.response.ConsultingSessionResponse;
import com.kefa.common.exception.ConsultingSessionException;
import com.kefa.common.exception.ErrorCode;
import com.kefa.common.exception.FeedbackException;
import com.kefa.domain.entity.Company;
import com.kefa.domain.entity.ConsultingSession;
import com.kefa.domain.entity.ConsultingSessionFeedback;
import com.kefa.domain.type.ConsultingStatus;
import com.kefa.infrastructure.repository.CompanyRepository;
import com.kefa.infrastructure.repository.ConsultingSessionFeedbackRepository;
import com.kefa.infrastructure.repository.ConsultingSessionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class ConsultingSessionUseCase {

    private final ConsultingSessionFeedbackRepository consultingSessionFeedbackRepository;
    private final ConsultingSessionRepository consultingSessionRepository;
    private final CompanyRepository companyRepository;

    public void deleteFeedback(Long feedbackId, Long loginAccountId) {
        ConsultingSessionFeedback feedback = consultingSessionFeedbackRepository.findById(feedbackId)
            .orElseThrow(() -> new FeedbackException(ErrorCode.NOT_FOUND_CONSULTING_SESSION_FEEDBACK));

        validateAuthor(feedback.getAccountId(), loginAccountId);

        consultingSessionFeedbackRepository.delete(feedback);
    }

    public ConsultingSessionFeedbackDto updateFeedback(Long feedbackId, UpdateConsultingFeedbackRequest request, Long loginAccountId) {
        ConsultingSessionFeedback feedback = consultingSessionFeedbackRepository.findById(feedbackId)
            .orElseThrow(() -> new FeedbackException(ErrorCode.NOT_FOUND_CONSULTING_SESSION_FEEDBACK));

        validateAuthor(feedback.getAccountId(), loginAccountId);

        feedback.updateContentAndType(request.getContent(), request.getFeedbackType());

        return ConsultingSessionFeedbackDto.from(consultingSessionFeedbackRepository.save(feedback));
    }

    public ConsultingSessionFeedbackDto addFeedback(AddConsultingFeedbackCommand command) {

        ConsultingSession consultingSession = consultingSessionRepository.findByIdWithCompanyAndParticipant(command.getConsultingSessionId())
            .orElseThrow(() -> new ConsultingSessionException(ErrorCode.NOT_FOUND_CONSULTING_SESSION));

        validateCompany(consultingSession.getCompany().getId(), command.getCompanyId());
        validateParticipant(consultingSession.getParticipantAccountIds(), command.getLoginAccount().getId());

        ConsultingSessionFeedback consultingSessionFeedback = ConsultingSessionFeedback.builder()
            .accountId(command.getLoginAccount().getId())
            .name(command.getLoginAccount().getName())
            .content(command.getRequest().getContent())
            .feedbackType(command.getRequest().getFeedbackType())
            .consultingSession(consultingSession)
            .build();

        consultingSession.addFeedback(consultingSessionFeedback);
        consultingSessionRepository.save(consultingSession);

        return ConsultingSessionFeedbackDto.from(consultingSessionFeedbackRepository.save(consultingSessionFeedback));
    }

    public List<ConsultingSessionResponse> getAllFromCompany(Long companyId) {
        return consultingSessionRepository.findAllByCompanyId(companyId).stream().map(ConsultingSessionResponse::from).toList();
    }

    public ConsultingSessionDetail getDetail(Long companyId, Long consultingSessionId) {

        ConsultingSession consultingSession = consultingSessionRepository.findByIdWithCompanyAndDocumentsAndFeedback(consultingSessionId)
            .orElseThrow(() -> new ConsultingSessionException(ErrorCode.NOT_FOUND_CONSULTING_SESSION));

        validateCompanyId(consultingSession.getCompany().getId(), companyId);

        List<ConsultingSessionFeedback> feedbacks = consultingSessionRepository.findFeedbacksBySessionId(consultingSessionId);

        return ConsultingSessionDetail.of(consultingSession, feedbacks);
    }

    public ConsultingSessionResponse add(Long companyId, Long loginAccountId) {

        Company company = companyRepository.findById(companyId).orElseThrow(() -> new ConsultingSessionException(ErrorCode.COMPANY_NOT_FOUND));

        ConsultingSession consultingSession = consultingSessionRepository.save(com.kefa.domain.entity.ConsultingSession.builder()
            .status(ConsultingStatus.WAITING)
            .company(company)
            .build());

        consultingSession.addParticipantAccountId(loginAccountId);
        company.addConsultingSession(consultingSession);

        return ConsultingSessionResponse.from(consultingSession);
    }

    @Transactional(readOnly = true)
    public void validateAccountIsParticipant(Long consultingSessionId, Long loginAccountId) {

        ConsultingSession consultingSession = consultingSessionRepository.findByIdWithParticipants(consultingSessionId)
            .orElseThrow(() -> new ConsultingSessionException(ErrorCode.NOT_FOUND_CONSULTING_SESSION));

        if (!consultingSession.getParticipantAccountIds().contains(loginAccountId)) {
            throw new ConsultingSessionException(ErrorCode.CONSULTING_ACCESS_DENIED);
        }

    }

    private void validateAuthor(Long getAccountId, Long loginAccountId) {
        if (!getAccountId.equals(loginAccountId)) {
            throw new FeedbackException(ErrorCode.FORBIDDEN_FEEDBACK_UPDATE);
        }
    }

    private void validateParticipant(Set<Long> participants, Long loginAccountId) {
        if (!participants.contains(loginAccountId)) {
            throw new ConsultingSessionException(ErrorCode.CONSULTING_ACCESS_DENIED);
        }
    }

    private void validateCompany(Long getCompanyId, Long requestCompanyId) {
        if (!getCompanyId.equals(requestCompanyId)) {
            throw new ConsultingSessionException(ErrorCode.INVALID_COMPANY);
        }
    }

    private void validateCompanyId(Long getCompanyId, Long requestCompanyId) {
        if (!getCompanyId.equals(requestCompanyId)) {
            throw new ConsultingSessionException(ErrorCode.INVALID_COMPANY);
        }
    }
}
