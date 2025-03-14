package com.kefa.api.controller;

import com.kefa.api.dto.consulting.response.ConsultingSessionResponse;
import com.kefa.application.service.ConsultingSessionService;
import com.kefa.common.response.ApiResponse;
import com.kefa.infrastructure.security.auth.LoginAccount;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@PreAuthorize("hasAnyRole('CONCIERGE_ACCOUNT', 'EXPERT', 'ADMIN', 'STAFF')")
@RestController
@RequiredArgsConstructor
public class ConsultingController {

    private final ConsultingSessionService consultingSessionService;

    @PostMapping("/company/{companyId}/consulting")
    public ApiResponse<ConsultingSessionResponse> addConsultingSession(@PathVariable Long companyId, @AuthenticationPrincipal LoginAccount loginAccount) {
        return ApiResponse.success(consultingSessionService.add(companyId, loginAccount.getId()));
    }

    @GetMapping("/company/{companyId}/consulting/{consultingSessionId}")
    public ApiResponse<ConsultingSessionResponse> getConsultingSession(@PathVariable Long companyId, @PathVariable Long consultingSessionId, @AuthenticationPrincipal LoginAccount loginAccount) {
        return ApiResponse.success(consultingSessionService.getDetail(companyId, consultingSessionId, loginAccount.getId()));
    }

    @GetMapping("/company/{companyId}/consulting")
    public ApiResponse<List<ConsultingSessionResponse>> getAllFromCompany(@PathVariable Long companyId, @AuthenticationPrincipal LoginAccount loginAccount) {
        return ApiResponse.success(consultingSessionService.getAllFromCompany(companyId, loginAccount.getId()));
    }

}
