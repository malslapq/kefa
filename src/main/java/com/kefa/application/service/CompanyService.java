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

    /**
     * Updates a company's business number after validating the business information internally and externally.
     *
     * First validates the provided business information using internal logic, then performs external validation via the NTS client.
     * If both validations succeed, updates the company's business number using the external validation response.
     *
     * @param companyId the ID of the company to update
     * @param request the business information to validate and update
     * @param loginAccountId the ID of the account performing the update
     * @return the updated company information
     */
    public CompanyResponse updateBusinessNumber(Long companyId, BusinessValidateRequest request, Long loginAccountId) {
        companyUseCase.validateBusinessInfo(companyId, request, loginAccountId);
        BusinessValidateResponse ntsResponse = client.validateBusinessInfo(request);
        return companyUseCase.updateBusinessNumber(companyId, request, ntsResponse);
    }

    /**
     * Deletes a company identified by its ID on behalf of the specified user.
     *
     * @param companyId       the unique identifier of the company to delete
     * @param loginAccountId  the ID of the user performing the deletion
     */
    public void delete(Long companyId, Long loginAccountId) {
        companyUseCase.delete(companyId, loginAccountId);
    }

    /**
     * Updates the details of a company with the specified ID.
     *
     * @param companyId the unique identifier of the company to update
     * @param request the updated company information
     * @param loginAccountId the ID of the account performing the update
     * @return the updated company information
     */
    public CompanyResponse updateCompany(Long companyId, CompanyUpdateRequest request, Long loginAccountId) {
        return companyUseCase.update(companyId, request, loginAccountId);
    }

    /**
     * Retrieves the company associated with the specified user and target ID.
     *
     * @param targetId the identifier of the company to retrieve
     * @param loginAccountId the identifier of the user requesting the company
     * @return the company information for the specified user and target
     */
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
