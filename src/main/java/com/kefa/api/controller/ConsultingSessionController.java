package com.kefa.api.controller;

import com.kefa.api.dto.consulting.command.ConsultingFeedbackAddCommand;
import com.kefa.api.dto.consulting.request.ConsultingFeedbackAddRequest;
import com.kefa.api.dto.consulting.request.ConsultingFeedbackUpdateRequest;
import com.kefa.api.dto.consulting.response.ConsultingSessionDetail;
import com.kefa.api.dto.consulting.response.ConsultingSessionResponse;
import com.kefa.api.dto.consulting.response.ConsultingSessionFeedbackDto;
import com.kefa.application.service.ConsultingSessionService;
import com.kefa.common.response.ApiResponse;
import com.kefa.infrastructure.security.auth.LoginAccount;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@PreAuthorize("hasAnyRole('CONCIERGE_ACCOUNT', 'EXPERT', 'ADMIN', 'STAFF')")
@RestController
@RequiredArgsConstructor
public class ConsultingSessionController {

    private final ConsultingSessionService consultingSessionService;

    @DeleteMapping("/company/consulting/feedback/{feedbackId}")
    public ApiResponse<String> deleteFeedback(@PathVariable Long feedbackId, @AuthenticationPrincipal LoginAccount loginAccount) {
        consultingSessionService.deleteFeedback(feedbackId, loginAccount.getId());
        return ApiResponse.success("삭제 성공");
    }

    /**
     * Updates an existing consulting feedback entry identified by its ID.
     *
     * @param feedbackId the ID of the feedback to update
     * @param request the validated request containing updated feedback data
     * @return the updated consulting session feedback DTO wrapped in a success response
     */
    @PutMapping("/company/consulting/feedback/{feedbackId}")
    public ApiResponse<ConsultingSessionFeedbackDto> updateConsultingFeedback(@PathVariable Long feedbackId,
                                                                              @RequestBody @Valid ConsultingFeedbackUpdateRequest request,
                                                                              @AuthenticationPrincipal LoginAccount loginAccount) {
        return ApiResponse.success(consultingSessionService.updateFeedback(feedbackId, request, loginAccount.getId()));
    }

    /**
     * Adds new feedback to a specific consulting session for a company.
     *
     * @param companyId the ID of the company
     * @param consultingSessionId the ID of the consulting session
     * @param request the feedback details to add
     * @return the created consulting session feedback DTO wrapped in a success response
     */
    @PostMapping("/company/{companyId}/consulting/{consultingSessionId}/feedback")
    public ApiResponse<ConsultingSessionFeedbackDto> addFeedback(@PathVariable Long companyId,
                                                                 @PathVariable Long consultingSessionId,
                                                                 @RequestBody @Valid ConsultingFeedbackAddRequest request,
                                                                 @AuthenticationPrincipal LoginAccount loginAccount
    ){

        ConsultingFeedbackAddCommand consultingFeedbackAddCommand = ConsultingFeedbackAddCommand.builder()
            .companyId(companyId)
            .consultingSessionId(consultingSessionId)
            .loginAccount(loginAccount)
            .request(request)
            .build();

        return ApiResponse.success(consultingSessionService.addFeedback(consultingFeedbackAddCommand));
    }

    /**
     * Creates a new consulting session for the specified company.
     *
     * @param companyId the ID of the company for which to create the consulting session
     * @return the created consulting session response wrapped in an ApiResponse
     */
    @PostMapping("/company/{companyId}/consulting")
    public ApiResponse<ConsultingSessionResponse> addConsultingSession(@PathVariable Long companyId, @AuthenticationPrincipal LoginAccount loginAccount) {
        return ApiResponse.success(consultingSessionService.add(companyId, loginAccount.getId()));
    }

    @GetMapping("/company/{companyId}/consulting/{consultingSessionId}")
    public ApiResponse<ConsultingSessionDetail> getConsultingSessionDetail(@PathVariable Long companyId, @PathVariable Long consultingSessionId, @AuthenticationPrincipal LoginAccount loginAccount) {
        return ApiResponse.success(consultingSessionService.getDetail(companyId, consultingSessionId, loginAccount.getId()));
    }

    @GetMapping("/company/{companyId}/consulting")
    public ApiResponse<List<ConsultingSessionResponse>> getAllFromCompany(@PathVariable Long companyId, @AuthenticationPrincipal LoginAccount loginAccount) {
        return ApiResponse.success(consultingSessionService.getAllFromCompany(companyId, loginAccount.getId()));
    }

}
