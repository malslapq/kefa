package com.kefa.application.usecase;

import com.kefa.common.exception.ErrorCode;
import com.kefa.common.exception.NtsException;
import com.kefa.infrastructure.client.nts.BusinessNoValidateResponse;
import com.kefa.infrastructure.client.nts.BusinessStatusData;
import com.kefa.infrastructure.repository.CompanyRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CompanyUseCase {

    private final CompanyRepository companyRepository;


    public void validateBusinessNumber(BusinessNoValidateResponse ntsApiResponse) {

        validateResponseData(ntsApiResponse);

        BusinessStatusData data = ntsApiResponse.getData().get(0);

        validateBusinessNumberActive(data);

    }

    private void validateResponseData(BusinessNoValidateResponse response) {
        if (response.getData() == null || response.getData().isEmpty()) {
            throw new NtsException(ErrorCode.BUSINESS_NUMBER_NOT_FOUND);
        }
    }

    private void validateBusinessNumberActive(BusinessStatusData data) {

        if(data.isNotRegistered()) {
            throw new NtsException(ErrorCode.BUSINESS_NUMBER_NOT_FOUND);
        }

        if (!data.isActive()) {
            throw new NtsException(ErrorCode.INACTIVE_BUSINESS_NUMBER);
        }

    }

}
