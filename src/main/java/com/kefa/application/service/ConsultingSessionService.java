package com.kefa.application.service;

import com.kefa.api.dto.consulting.command.ConsultingFeedbackAddCommand;
import com.kefa.api.dto.consulting.request.ConsultingFeedbackUpdateRequest;
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

    /**
     * Deletes a consulting session feedback entry identified by its ID for the specified account.
     *
     * @param feedbackId      the ID of the feedback to delete
     * @param loginAccountId  the ID of the account performing the deletion
     */
    public void deleteFeedback(Long feedbackId, Long loginAccountId) {
        consultingSessionUseCase.deleteFeedback(feedbackId, loginAccountId);
    }

    /**
     * Updates an existing consulting session feedback entry with new data.
     *
     * @param feedbackId the ID of the feedback to update
     * @param request the updated feedback data
     * @param loginAccountId the ID of the account performing the update
     * @return the updated consulting session feedback DTO
     */
    public ConsultingSessionFeedbackDto updateFeedback(Long feedbackId, ConsultingFeedbackUpdateRequest request, Long loginAccountId) {
        return consultingSessionUseCase.updateFeedback(feedbackId, request, loginAccountId);
    }

    /**
     * Adds feedback to a consulting session after validating that the account is a participant.
     *
     * @param consultingFeedbackAddCommand the command containing feedback details and account information
     * @return the created consulting session feedback DTO
     */
    @Transactional
    public ConsultingSessionFeedbackDto addFeedback(ConsultingFeedbackAddCommand consultingFeedbackAddCommand) {
        consultingSessionUseCase.validateAccountIsParticipant(
            consultingFeedbackAddCommand.getConsultingSessionId(), consultingFeedbackAddCommand.getLoginAccount().getId());

        return consultingSessionUseCase.addFeedback(consultingFeedbackAddCommand);
    }

    /**
     * Retrieves all consulting sessions associated with a specified company after validating ownership and permissions.
     *
     * @param companyId the ID of the company whose consulting sessions are to be retrieved
     * @param loginAccountId the ID of the account requesting the sessions
     * @return a list of consulting session responses for the specified company
     */
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
