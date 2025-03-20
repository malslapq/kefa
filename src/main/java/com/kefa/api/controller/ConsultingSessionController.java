package com.kefa.api.controller;

import com.kefa.api.dto.consulting.command.AddFeedbackCommand;
import com.kefa.api.dto.consulting.request.AddFeedbackRequest;
import com.kefa.api.dto.consulting.response.ConsultingSessionDetailResponse;
import com.kefa.api.dto.consulting.response.ConsultingSessionResponse;
import com.kefa.api.dto.consulting.response.FeedbackResponse;
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

    @PostMapping("/company/{companyId}/consulting/{consultingSessionId}/feedback")
    public ApiResponse<FeedbackResponse> addFeedback(@PathVariable Long companyId, @PathVariable Long consultingSessionId, @RequestBody @Valid AddFeedbackRequest request, @AuthenticationPrincipal LoginAccount loginAccount){

        AddFeedbackCommand addFeedbackCommand = AddFeedbackCommand.builder()
            .companyId(companyId)
            .consultingSessionId(consultingSessionId)
            .loginAccountId(loginAccount.getId())
            .request(request)
            .build();

        return ApiResponse.success(consultingSessionService.addFeedback(addFeedbackCommand));
    }

    @PostMapping("/company/{companyId}/consulting")
    public ApiResponse<ConsultingSessionResponse> addConsultingSession(@PathVariable Long companyId, @AuthenticationPrincipal LoginAccount loginAccount) {
        return ApiResponse.success(consultingSessionService.add(companyId, loginAccount.getId()));
    }

    @GetMapping("/company/{companyId}/consulting/{consultingSessionId}")
    public ApiResponse<ConsultingSessionDetailResponse> getConsultingSession(@PathVariable Long companyId, @PathVariable Long consultingSessionId, @AuthenticationPrincipal LoginAccount loginAccount) {
        return ApiResponse.success(consultingSessionService.getDetail(companyId, consultingSessionId, loginAccount.getId()));
    }

    @GetMapping("/company/{companyId}/consulting")
    public ApiResponse<List<ConsultingSessionResponse>> getAllFromCompany(@PathVariable Long companyId, @AuthenticationPrincipal LoginAccount loginAccount) {
        return ApiResponse.success(consultingSessionService.getAllFromCompany(companyId, loginAccount.getId()));
    }

}
