package com.kefa.application.service;

import com.kefa.api.dto.company.request.*;
import com.kefa.api.dto.company.response.CompanyAddResponse;
import com.kefa.api.dto.company.response.CompanyResponse;
import com.kefa.application.usecase.CompanyUseCase;
import com.kefa.infrastructure.client.nts.dto.status.BusinessStatusResponse;
import com.kefa.infrastructure.client.nts.dto.validate.BusinessValidateResponse;
import com.kefa.infrastructure.client.nts.service.NtsBusinessValidationClient;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CompanyService {

    private final CompanyUseCase companyUseCase;

    private final NtsBusinessValidationClient client;

    public CompanyResponse updateBusinessNumber(Long companyId, BusinessValidateRequest request, Long loginAccountId) {
        companyUseCase.validateBusinessInfo(companyId, request, loginAccountId);
        BusinessValidateResponse ntsResponse = client.validateBusinessInfo(request);
        return companyUseCase.updateBusinessNumber(companyId, request, ntsResponse);
    }

    public void delete(Long companyId, CompanyDeleteRequest request, Long loginAccountId) {
        companyUseCase.delete(companyId, request, loginAccountId);
    }

    public CompanyResponse updateCompany(Long companyId, CompanyUpdateRequest request, Long loginAccountId) {
        return companyUseCase.update(companyId, request, loginAccountId);
    }

    public CompanyResponse getMyCompany(Long targetId, Long loginAccountId) {
        return companyUseCase.getMyCompany(targetId, loginAccountId);
    }

    public List<CompanyResponse> getMyCompanies(Long loginAccountId) {
        return companyUseCase.getMyCompanies(loginAccountId);
    }

    public CompanyAddResponse add(CompanyAddRequest companyAddRequest, Long loginAccountId) {
        return companyUseCase.add(companyAddRequest, loginAccountId);
    }

    public BusinessStatusResponse validateBusinessNumber(BusinessNumberValidateRequest request) {
        BusinessStatusResponse ntsApiResponse = client.validateBusinessNumber(request);
        companyUseCase.validateBusinessNumber(ntsApiResponse);
        return ntsApiResponse;
    }
}
