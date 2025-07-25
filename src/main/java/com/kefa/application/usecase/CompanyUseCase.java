package com.kefa.application.usecase;

import com.kefa.api.dto.company.request.BusinessValidateRequest;
import com.kefa.api.dto.company.request.CompanyAddRequest;
import com.kefa.api.dto.company.request.CompanyDeleteRequest;
import com.kefa.api.dto.company.request.CompanyUpdateRequest;
import com.kefa.api.dto.company.response.CompanyAddResponse;
import com.kefa.api.dto.company.response.CompanyResponse;
import com.kefa.common.exception.AuthenticationException;
import com.kefa.common.exception.CompanyException;
import com.kefa.common.exception.ErrorCode;
import com.kefa.domain.entity.Account;
import com.kefa.domain.entity.Company;
import com.kefa.infrastructure.client.nts.dto.status.BusinessStatusData;
import com.kefa.infrastructure.client.nts.dto.status.BusinessStatusResponse;
import com.kefa.infrastructure.client.nts.dto.validate.BusinessValidateData;
import com.kefa.infrastructure.client.nts.dto.validate.BusinessValidateResponse;
import com.kefa.infrastructure.repository.AccountRepository;
import com.kefa.infrastructure.repository.CompanyRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CompanyUseCase {

    private final CompanyRepository companyRepository;

    private final AccountRepository accountRepository;

    private final PasswordEncoder passwordEncoder;

    @Transactional(readOnly = true)
    public void validateCompanyOwnershipAndProcess(Long companyId, Long loginAccountId) {

        Company company = getCompanyById(companyId);
        validateCompanyOwnership(loginAccountId, company.getAccount().getId());

    }

    @Transactional
    public CompanyResponse updateBusinessNumber(Long companyId, BusinessValidateRequest request, BusinessValidateResponse ntsResponse) {

        validateBusinessInfoMatch(request, ntsResponse);

        Company company = companyRepository.findById(companyId).orElseThrow(() -> new CompanyException(ErrorCode.COMPANY_NOT_FOUND));

        company.updateBusinessNumber(request.getB_no());

        return CompanyResponse.from(company);
    }

    public void validateBusinessInfo(Long companyId, BusinessValidateRequest request, Long loginAccountId) {

        Company company = getCompanyById(companyId);

        validateCompanyOwnership(loginAccountId, company.getAccount().getId());

        validateDuplicateBusinessNumber(request.getB_no());

    }

    @Transactional
    public void delete(Long companyId, Long loginAccountId) {

        Company company = getCompanyById(companyId);

        validateCompanyOwnership(loginAccountId, company.getAccount().getId());

        companyRepository.delete(company);

    }

    @Transactional
    public CompanyResponse update(Long companyId, CompanyUpdateRequest request, Long loginAccountId) {

        Company company = getCompanyById(companyId);

        validateCompanyOwnership(loginAccountId, company.getAccount().getId());

        company.update(request);

        return CompanyResponse.from(company);

    }

    @Transactional(readOnly = true)
    public CompanyResponse getMyCompany(Long targetId, Long loginAccountId) {

        Company company = getCompanyById(targetId);

        validateCompanyOwnership(loginAccountId, company.getAccount().getId());

        return CompanyResponse.from(company);

    }

    @Transactional(readOnly = true)
    public List<CompanyResponse> getMyCompanies(Long loginAccountId) {
        return companyRepository.findAllByAccountIdOrderByCreatedAtDesc(loginAccountId).stream().map(CompanyResponse::from).toList();
    }

    public CompanyAddResponse add(CompanyAddRequest request, Long loginAccountId) {

        Account account = accountRepository.findById(loginAccountId).orElseThrow(() -> new AuthenticationException(ErrorCode.NOT_FOUND_ACCOUNT));
        Company company = CompanyAddRequest.toEntity(request, account);

        companyRepository.save(company);

        return CompanyAddResponse.from(company);

    }

    public void validateBusinessNumber(BusinessStatusResponse ntsApiResponse) {

        validateResponseData(ntsApiResponse);

        BusinessStatusData data = ntsApiResponse.getData().get(0);

        validateBusinessNumberActive(data);

    }

    private void validateBusinessInfoMatch(BusinessValidateRequest request, BusinessValidateResponse ntsResponse) {
        BusinessValidateData ntsData = ntsResponse.getData().get(0);

        // 사업자번호 일치 확인
        if (!request.getB_no().equals(ntsData.getRequestParam().getB_no())) {
            throw new CompanyException(ErrorCode.BUSINESS_NUMBER_NOT_FOUND);
        }

        // 대표자명 일치 확인
        if (request.getP_nm() != null && !request.getP_nm().equals(ntsData.getRequestParam().getP_nm())) {
            throw new CompanyException(ErrorCode.INVALID_REPRESENTATIVE_INFO);
        }

        // 회사명 일치 확인
        if (request.getB_nm() != null && !request.getB_nm().equals(ntsData.getRequestParam().getB_nm())) {
            throw new CompanyException(ErrorCode.INVALID_COMPANY_INFO);
        }

        // 사업자 상태 확인 01 -> 계속사업자
        if (!"01".equals(ntsData.getStatus().getBusinessStatus())) {
            throw new CompanyException(ErrorCode.INACTIVE_BUSINESS_NUMBER);
        }
    }

    private void validateDuplicateBusinessNumber(String businessNumber) {
        if (companyRepository.existsByBusinessNumberAndDeletedFalse(businessNumber)) {
            throw new CompanyException(ErrorCode.DUPLICATE_BUSINESS_NUMBER);
        }
    }

    private void validateCompanyOwnership(Long loginUserId, Long companyAccountId) {
        if (!companyAccountId.equals(loginUserId)) {
            throw new CompanyException(ErrorCode.NOT_COMPANY_OWNER);
        }
    }

    private void validateResponseData(BusinessStatusResponse response) {
        if (response.getData() == null || response.getData().isEmpty()) {
            throw new CompanyException(ErrorCode.BUSINESS_NUMBER_NOT_FOUND);
        }
    }

    private void validateBusinessNumberActive(BusinessStatusData data) {

        if (data.isNotRegistered()) {
            throw new CompanyException(ErrorCode.BUSINESS_NUMBER_NOT_FOUND);
        }

        if (!data.isActive()) {
            throw new CompanyException(ErrorCode.INACTIVE_BUSINESS_NUMBER);
        }

    }

    private Company getCompanyById(Long companyId) {
        return companyRepository.findCompanyById(companyId).orElseThrow(() -> new CompanyException(ErrorCode.COMPANY_NOT_FOUND));
    }


}
