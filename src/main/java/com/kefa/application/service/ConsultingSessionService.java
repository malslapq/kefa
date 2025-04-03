package com.kefa.application.service;

import com.kefa.api.dto.consulting.command.AddConsultingFeedbackCommand;
import com.kefa.api.dto.consulting.response.ConsultingSessionDetail;
import com.kefa.api.dto.consulting.response.ConsultingSessionResponse;
import com.kefa.api.dto.consulting.response.ConsultingSessionFeedbackDto;
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

    public ConsultingSessionFeedbackDto addFeedback(AddConsultingFeedbackCommand addConsultingFeedbackCommand) {
        consultingSessionUseCase.validateAccountIsParticipant(
            addConsultingFeedbackCommand.getConsultingSessionId(), addConsultingFeedbackCommand.getLoginAccount().getId());

        return consultingSessionUseCase.addFeedback(addConsultingFeedbackCommand);
    }

    @Transactional(readOnly = true)
    public List<ConsultingSessionResponse> getAllFromCompany(Long companyId, Long loginAccountId) {
        companyUseCase.validateCompanyOwnershipAndProcess(companyId, loginAccountId);
        return consultingSessionUseCase.getAllFromCompany(companyId);
    }

    @Transactional(readOnly = true)
    public ConsultingSessionDetail getDetail(Long companyId, Long consultingSessionId, Long loginAccountId) {
        companyUseCase.validateCompanyOwnershipAndProcess(companyId, loginAccountId);
        return consultingSessionUseCase.getDetail(companyId, consultingSessionId);
    }

    @Transactional
    public ConsultingSessionResponse add(Long companyId, Long loginAccountId) {

        companyUseCase.validateCompanyOwnershipAndProcess(companyId, loginAccountId);
        return consultingSessionUseCase.add(companyId, loginAccountId);

    }
}
