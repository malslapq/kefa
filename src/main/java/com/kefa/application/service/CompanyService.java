package com.kefa.application.service;

import com.kefa.api.dto.company.request.BusinessNumberValidateRequest;
import com.kefa.infrastructure.client.nts.BusinessNoValidateResponse;
import com.kefa.application.usecase.CompanyUseCase;
import com.kefa.infrastructure.client.nts.ValidationBusinessNumberClient;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CompanyService {

    private final CompanyUseCase companyUseCase;
    private final ValidationBusinessNumberClient client;

    public BusinessNoValidateResponse validateBusinessNumber(BusinessNumberValidateRequest request) {
        BusinessNoValidateResponse ntsApiResponse = client.validateBusinessNumber(request);
        companyUseCase.validateBusinessNumber(ntsApiResponse);
        return ntsApiResponse;
    }
}
