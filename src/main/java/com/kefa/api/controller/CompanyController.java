package com.kefa.api.controller;

import com.kefa.api.dto.company.request.*;
import com.kefa.api.dto.company.response.CompanyAddResponse;
import com.kefa.api.dto.company.response.CompanyResponse;
import com.kefa.application.service.CompanyService;
import com.kefa.common.response.ApiResponse;
import com.kefa.infrastructure.client.nts.dto.status.BusinessStatusResponse;
import com.kefa.infrastructure.security.auth.AuthenticationInfo;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class CompanyController {

    private final CompanyService companyService;

    @DeleteMapping("/companies/{companyId}")
    public ApiResponse<Void> deleteCompany(@PathVariable Long companyId, @RequestBody @Valid CompanyDeleteRequest request, Authentication authentication) {
        companyService.delete(companyId, request, AuthenticationInfo.from(authentication));
        return ApiResponse.success();
    }

    @PatchMapping("/company/{companyId}")
    public ApiResponse<CompanyResponse> updateBusinessNumber(@PathVariable Long companyId, @RequestBody @Valid BusinessValidateRequest request, Authentication authentication) {
        return ApiResponse.success(companyService.updateBusinessNumber(companyId, request, AuthenticationInfo.from(authentication)));
    }

    @PutMapping("/company")
    public ApiResponse<CompanyResponse> updateCompany(@RequestBody @Valid CompanyUpdateRequest request, Authentication authentication) {
        return ApiResponse.success(companyService.updateCompany(request, AuthenticationInfo.from(authentication)));
    }

    @GetMapping("/companies/{companyId}")
    public ApiResponse<CompanyResponse> getCompany(@PathVariable Long companyId, Authentication authentication) {
        return ApiResponse.success(companyService.getMyCompany(companyId, AuthenticationInfo.from(authentication)));
    }

    @GetMapping("/companies")
    public ApiResponse<List<CompanyResponse>> getCompanies(Authentication authentication) {
        return ApiResponse.success(companyService.getMyCompanies(AuthenticationInfo.from(authentication)));
    }

    @PostMapping("/companies")
    public ApiResponse<CompanyAddResponse> add(@RequestBody @Valid CompanyAddRequest companyAddRequest, Authentication authentication) {
        return ApiResponse.success(companyService.add(companyAddRequest, AuthenticationInfo.from(authentication)));
    }

    @PostMapping("/companies/validate-business-number")
    public ApiResponse<BusinessStatusResponse> validateBusinessNumber(@RequestBody @Valid BusinessNumberValidateRequest request) {
        return ApiResponse.success(companyService.validateBusinessNumber(request));
    }

}