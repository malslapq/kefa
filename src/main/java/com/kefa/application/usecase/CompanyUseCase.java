package com.kefa.application.usecase;

import com.kefa.api.dto.company.request.CompanyAddRequest;
import com.kefa.api.dto.company.request.CompanyUpdateRequest;
import com.kefa.api.dto.company.response.CompanyAddResponse;
import com.kefa.api.dto.company.response.CompanyResponse;
import com.kefa.common.exception.AuthenticationException;
import com.kefa.common.exception.CompanyException;
import com.kefa.common.exception.ErrorCode;
import com.kefa.common.exception.NtsException;
import com.kefa.common.response.ApiResponse;
import com.kefa.domain.entity.Account;
import com.kefa.domain.entity.Company;
import com.kefa.infrastructure.client.nts.BusinessNoValidateResponse;
import com.kefa.infrastructure.client.nts.BusinessStatusData;
import com.kefa.infrastructure.repository.AccountRepository;
import com.kefa.infrastructure.repository.CompanyRepository;
import com.kefa.infrastructure.security.auth.AuthenticationInfo;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CompanyUseCase {

    private final CompanyRepository companyRepository;

    private final AccountRepository accountRepository;

    @Transactional
    public CompanyResponse update(CompanyUpdateRequest request, AuthenticationInfo authenticationInfo) {

        Company company = getCompanyById(request.getId());

        validateCompanyOwnership(authenticationInfo.getId(), company.getAccount().getId());

        company.update(request);

        return CompanyResponse.from(company);

    }

    @Transactional(readOnly = true)
    public CompanyResponse getMyCompany(Long targetId, AuthenticationInfo authenticationInfo) {

        Company company = getCompanyById(targetId);

        validateCompanyOwnership(authenticationInfo.getId(), company.getAccount().getId());

        return CompanyResponse.from(company);

    }

    @Transactional(readOnly = true)
    public List<CompanyResponse> getMyCompanies(AuthenticationInfo authenticationInfo) {
        return companyRepository.findAllByAccountId(authenticationInfo.getId()).stream().map(CompanyResponse::from).toList();
    }

    public CompanyAddResponse add(CompanyAddRequest request, AuthenticationInfo authenticationInfo) {

        Account account = accountRepository.findById(authenticationInfo.getId()).orElseThrow(() -> new AuthenticationException(ErrorCode.ACCOUNT_NOT_FOUND));
        Company company = CompanyAddRequest.toEntity(request, account);

        companyRepository.save(company);

        return CompanyAddResponse.from(company);

    }

    public void validateBusinessNumber(BusinessNoValidateResponse ntsApiResponse) {

        validateResponseData(ntsApiResponse);

        BusinessStatusData data = ntsApiResponse.getData().get(0);

        validateBusinessNumberActive(data);

    }

    private void validateCompanyOwnership(Long loginUserId, Long companyAccountId) {
        if(!companyAccountId.equals(loginUserId)){
            throw new CompanyException(ErrorCode.NOT_COMPANY_OWNER);
        }
    }

    private void validateResponseData(BusinessNoValidateResponse response) {
        if (response.getData() == null || response.getData().isEmpty()) {
            throw new NtsException(ErrorCode.BUSINESS_NUMBER_NOT_FOUND);
        }
    }

    private void validateBusinessNumberActive(BusinessStatusData data) {

        if (data.isNotRegistered()) {
            throw new NtsException(ErrorCode.BUSINESS_NUMBER_NOT_FOUND);
        }

        if (!data.isActive()) {
            throw new NtsException(ErrorCode.INACTIVE_BUSINESS_NUMBER);
        }

    }

    private Company getCompanyById(Long companyId) {
        return companyRepository.findCompanyById(companyId).orElseThrow(() -> new CompanyException(ErrorCode.COMPANY_NOT_FOUND));
    }
}
