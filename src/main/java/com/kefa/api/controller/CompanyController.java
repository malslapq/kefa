package com.kefa.api.controller;

import com.kefa.api.dto.company.request.BusinessNumberValidateRequest;
import com.kefa.infrastructure.client.nts.BusinessNoValidateResponse;
import com.kefa.application.service.CompanyService;
import com.kefa.common.response.ApiResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class CompanyController {

    private final CompanyService companyService;

    @PostMapping("/companies/validate-business-number")
    public ApiResponse<BusinessNoValidateResponse> validateBusinessNumber(@RequestBody @Valid BusinessNumberValidateRequest request) {
        return ApiResponse.success(companyService.validateBusinessNumber(request));
    }

}