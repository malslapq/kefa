package com.kefa.application.usecase;

import com.kefa.api.dto.consulting.response.ConsultingSessionDetailResponse;
import com.kefa.api.dto.consulting.response.ConsultingSessionResponse;
import com.kefa.common.exception.ConsultingSessionException;
import com.kefa.common.exception.ErrorCode;
import com.kefa.domain.entity.Company;
import com.kefa.domain.entity.ConsultingSession;
import com.kefa.domain.type.ConsultingStatus;
import com.kefa.infrastructure.repository.CompanyRepository;
import com.kefa.infrastructure.repository.ConsultingSessionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ConsultingSessionUseCase {

    private final ConsultingSessionRepository consultingSessionRepository;
    private final CompanyRepository companyRepository;

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
    public void validateUserIsParticipant(Long consultingSessionId, Long loginAccountId) {

        ConsultingSession consultingSession = consultingSessionRepository.findByIdWithParticipants(consultingSessionId)
            .orElseThrow(() -> new ConsultingSessionException(ErrorCode.NOT_FOUND_CONSULTING_SESSION));

        if(!consultingSession.getParticipantAccountIds().contains(loginAccountId)){
            throw new ConsultingSessionException(ErrorCode.CONSULTING_ACCESS_DENIED);
        }

    }

    private void validateCompanyId(Long getCompanyId, Long requestCompanyId) {
        if(!getCompanyId.equals(requestCompanyId)){
            throw new ConsultingSessionException(ErrorCode.INVALID_COMPANY);
        }
    }
}
