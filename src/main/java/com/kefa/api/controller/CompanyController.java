package com.kefa.api.controller;

import com.kefa.api.dto.company.request.*;
import com.kefa.api.dto.company.response.CompanyAddResponse;
import com.kefa.api.dto.company.response.CompanyResponse;
import com.kefa.application.service.CompanyService;
import com.kefa.common.response.ApiResponse;
import com.kefa.infrastructure.client.nts.dto.status.BusinessStatusResponse;
import com.kefa.infrastructure.security.auth.LoginAccount;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class CompanyController {

    private final CompanyService companyService;

    /**
     * Deletes a company by its ID for the authenticated user.
     *
     * @param companyId the ID of the company to delete
     * @return a success response with no content
     */
    @DeleteMapping("/companies/{companyId}")
    public ApiResponse<Void> deleteCompany(@PathVariable Long companyId, @RequestBody @Valid CompanyDeleteRequest request, @AuthenticationPrincipal LoginAccount loginAccount) {
        companyService.delete(companyId, loginAccount.getId());
        return ApiResponse.success();
    }

    /**
     * Updates the business number of a specific company for the authenticated user.
     *
     * @param companyId the ID of the company to update
     * @param request the business number validation request containing the new business number
     * @return the updated company information wrapped in a success response
     */
    @PatchMapping("/company/{companyId}/business-number")
    public ApiResponse<CompanyResponse> updateBusinessNumber(@PathVariable Long companyId, @RequestBody @Valid BusinessValidateRequest request, @AuthenticationPrincipal LoginAccount loginAccount) {
        return ApiResponse.success(companyService.updateBusinessNumber(companyId, request, loginAccount.getId()));
    }

    /**
     * Updates the details of a specific company for the authenticated user.
     *
     * @param companyId the ID of the company to update
     * @param request the updated company information
     * @return the updated company data wrapped in a success response
     */
    @PutMapping("/company/{companyId}")
    public ApiResponse<CompanyResponse> updateCompany(@PathVariable Long companyId, @RequestBody @Valid CompanyUpdateRequest request, @AuthenticationPrincipal LoginAccount loginAccount) {
        return ApiResponse.success(companyService.updateCompany(companyId, request, loginAccount.getId()));
    }

    /**
     * Retrieves a specific company associated with the authenticated user.
     *
     * @param companyId the ID of the company to retrieve
     * @return a success response containing the company details
     */
    @GetMapping("/companies/{companyId}")
    public ApiResponse<CompanyResponse> getCompany(@PathVariable Long companyId, @AuthenticationPrincipal LoginAccount loginAccount) {
        return ApiResponse.success(companyService.getMyCompany(companyId, loginAccount.getId()));
    }

    @GetMapping("/companies")
    public ApiResponse<List<CompanyResponse>> getCompanies(@AuthenticationPrincipal LoginAccount loginAccount) {
        return ApiResponse.success(companyService.getMyCompanies(loginAccount.getId()));
    }

    @PostMapping("/companies")
    public ApiResponse<CompanyAddResponse> add(@RequestBody @Valid CompanyAddRequest companyAddRequest, @AuthenticationPrincipal LoginAccount loginAccount) {
        return ApiResponse.success(companyService.add(companyAddRequest, loginAccount.getId()));
    }

    @PostMapping("/companies/validate-business-number")
    public ApiResponse<BusinessStatusResponse> validateBusinessNumber(@RequestBody @Valid BusinessNumberValidateRequest request) {
        return ApiResponse.success(companyService.validateBusinessNumber(request));
    }

}