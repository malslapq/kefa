package com.kefa.application.service;

import com.kefa.api.dto.consulting.response.ConsultingSessionDetailResponse;
import com.kefa.api.dto.consulting.response.ConsultingSessionResponse;
import com.kefa.application.usecase.CompanyUseCase;
import com.kefa.application.usecase.ConsultingSessionUseCase;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ConsultingSessionService {

    private final ConsultingSessionUseCase consultingSessionUseCase;
    private final CompanyUseCase companyUseCase;

    public List<ConsultingSessionResponse> getAllFromCompany(Long companyId, Long loginAccountId) {
        companyUseCase.validateCompanyOwnershipAndProcess(companyId, loginAccountId);
        return consultingSessionUseCase.getAllFromCompany(companyId);
    }

    public ConsultingSessionDetailResponse getDetail(Long companyId, Long consultingSessionId, Long loginAccountId) {
        companyUseCase.validateCompanyOwnershipAndProcess(companyId, loginAccountId);
        return consultingSessionUseCase.getDetail(companyId, consultingSessionId);
    }

    @Transactional
    public ConsultingSessionResponse add(Long companyId, Long loginAccountId) {

        companyUseCase.validateCompanyOwnershipAndProcess(companyId, loginAccountId);
        return consultingSessionUseCase.add(companyId, loginAccountId);

    }
}
