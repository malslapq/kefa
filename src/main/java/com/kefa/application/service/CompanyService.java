package com.kefa.application.service;

import com.kefa.api.dto.company.request.BusinessNumberValidateRequest;
import com.kefa.api.dto.company.request.CompanyAddRequest;
import com.kefa.api.dto.company.request.CompanyUpdateRequest;
import com.kefa.api.dto.company.response.CompanyAddResponse;
import com.kefa.api.dto.company.response.CompanyResponse;
import com.kefa.application.usecase.CompanyUseCase;
import com.kefa.common.response.ApiResponse;
import com.kefa.infrastructure.client.nts.BusinessNoValidateResponse;
import com.kefa.infrastructure.client.nts.ValidationBusinessNumberClient;
import com.kefa.infrastructure.security.auth.AuthenticationInfo;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CompanyService {

    private final CompanyUseCase companyUseCase;
    private final ValidationBusinessNumberClient client;

    public CompanyResponse updateCompany(CompanyUpdateRequest request, AuthenticationInfo authenticationInfo) {
        return companyUseCase.update(request, authenticationInfo);
    }

    public CompanyResponse getMyCompany(Long targetId, AuthenticationInfo from) {
        return companyUseCase.getMyCompany(targetId, from);
    }

    public List<CompanyResponse> getMyCompanies(AuthenticationInfo authenticationInfo) {
        return companyUseCase.getMyCompanies(authenticationInfo);
    }

    public CompanyAddResponse add(CompanyAddRequest companyAddRequest, AuthenticationInfo authenticationInfo) {
        return companyUseCase.add(companyAddRequest, authenticationInfo);
    }

    public BusinessNoValidateResponse validateBusinessNumber(BusinessNumberValidateRequest request) {
        BusinessNoValidateResponse ntsApiResponse = client.validateBusinessNumber(request);
        companyUseCase.validateBusinessNumber(ntsApiResponse);
        return ntsApiResponse;
    }
}
