package com.kefa.application.usecase;

import com.kefa.api.dto.consulting.command.AddFeedbackCommand;
import com.kefa.api.dto.consulting.response.ConsultingSessionDetailResponse;
import com.kefa.api.dto.consulting.response.ConsultingSessionResponse;
import com.kefa.api.dto.consulting.response.FeedbackResponse;
import com.kefa.common.exception.ConsultingSessionException;
import com.kefa.common.exception.ErrorCode;
import com.kefa.domain.entity.Company;
import com.kefa.domain.entity.ConsultingSession;
import com.kefa.domain.entity.Feedback;
import com.kefa.domain.type.ConsultingStatus;
import com.kefa.domain.type.FeedbackType;
import com.kefa.infrastructure.repository.CompanyRepository;
import com.kefa.infrastructure.repository.ConsultingSessionRepository;
import com.kefa.infrastructure.repository.FeedbackRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class ConsultingSessionUseCase {

    private final FeedbackRepository feedbackRepository;
    private final ConsultingSessionRepository consultingSessionRepository;
    private final CompanyRepository companyRepository;

    public FeedbackResponse addFeedback(AddFeedbackCommand command) {

        ConsultingSession consultingSession = consultingSessionRepository.findByIdWithCompanyAndParticipant(command.getConsultingSessionId())
            .orElseThrow(() -> new ConsultingSessionException(ErrorCode.NOT_FOUND_CONSULTING_SESSION));

        validateCompany(consultingSession.getCompany().getId(), command.getCompanyId());
        validateParticipant(consultingSession.getParticipantAccountIds(), command.getLoginAccountId());

        Feedback feedback = Feedback.builder()
            .targetId(command.getRequest().getTargetId())
            .accountId(command.getLoginAccountId())
            .parentId(command.getRequest().getParentId())
            .content(command.getRequest().getContent())
            .feedbackType(FeedbackType.SESSION)
            .build();

        return FeedbackResponse.from(feedbackRepository.save(feedback));
    }

    public List<ConsultingSessionResponse> getAllFromCompany(Long companyId) {
        return consultingSessionRepository.findAllByCompanyId(companyId).stream().map(ConsultingSessionResponse::from).toList();
    }

    public ConsultingSessionDetailResponse getDetail(Long companyId, Long consultingSessionId) {
        ConsultingSession consultingSession = consultingSessionRepository.findByIdWithDocumentsAndCompany(consultingSessionId).orElseThrow(() -> new ConsultingSessionException(ErrorCode.NOT_FOUND_CONSULTING_SESSION));

        validateCompanyId(consultingSession.getCompany().getId(), companyId);

        return ConsultingSessionDetailResponse.from(consultingSession);
    }

    public ConsultingSessionResponse add(Long companyId, Long loginAccountId) {

        Company company = companyRepository.findById(companyId).orElseThrow(() -> new ConsultingSessionException(ErrorCode.COMPANY_NOT_FOUND));

        ConsultingSession consultingSession = consultingSessionRepository.save(ConsultingSession.builder()
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
