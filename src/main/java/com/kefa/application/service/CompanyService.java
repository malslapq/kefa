package com.kefa.application.service;

import com.kefa.api.dto.company.request.BusinessNumberValidateRequest;
import com.kefa.api.dto.company.request.CompanyAddRequest;
import com.kefa.api.dto.company.response.CompanyAddResponse;
import com.kefa.application.usecase.CompanyUseCase;
import com.kefa.infrastructure.client.nts.BusinessNoValidateResponse;
import com.kefa.infrastructure.client.nts.ValidationBusinessNumberClient;
import com.kefa.infrastructure.security.auth.AuthenticationInfo;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CompanyService {

    private final CompanyUseCase companyUseCase;
    private final ValidationBusinessNumberClient client;

    public CompanyAddResponse add(CompanyAddRequest companyAddRequest, AuthenticationInfo authenticationInfo) {
        return companyUseCase.add(companyAddRequest, authenticationInfo);
    }

    public BusinessNoValidateResponse validateBusinessNumber(BusinessNumberValidateRequest request) {
        BusinessNoValidateResponse ntsApiResponse = client.validateBusinessNumber(request);
        companyUseCase.validateBusinessNumber(ntsApiResponse);
        return ntsApiResponse;
    }
}
