package com.kefa.application.usecase;

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

import java.util.List;

@Service
@RequiredArgsConstructor
public class ConsultingSessionUseCase {

    private final ConsultingSessionRepository consultingSessionRepository;
    private final CompanyRepository companyRepository;

    public List<ConsultingSessionResponse> getAllFromCompany(Long companyId) {
        return consultingSessionRepository.findAllByCompanyId(companyId).stream().map(ConsultingSessionResponse::from).toList();
    }

    public ConsultingSessionResponse getDetail(Long companyId, Long consultingSessionId) {

        return null;
    }

    public ConsultingSessionResponse add(Long companyId) {

        Company company = companyRepository.findById(companyId).orElseThrow(() -> new ConsultingSessionException(ErrorCode.COMPANY_NOT_FOUND));

        ConsultingSession consultingSession = consultingSessionRepository.save(ConsultingSession.builder()
            .status(ConsultingStatus.WAITING)
            .company(company)
            .build());

        company.addConsultingSession(consultingSession);

        return ConsultingSessionResponse.from(consultingSession);
    }
}
