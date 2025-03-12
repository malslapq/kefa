package com.kefa.application.usecase;

import com.kefa.api.dto.developmentItem.request.DevelopmentItemAddRequest;
import com.kefa.api.dto.developmentItem.response.DevelopmentItemResponse;
import com.kefa.common.exception.DevelopmentItemException;
import com.kefa.common.exception.ErrorCode;
import com.kefa.domain.entity.Company;
import com.kefa.domain.entity.DevelopmentItem;
import com.kefa.infrastructure.repository.CompanyRepository;
import com.kefa.infrastructure.repository.DevelopmentItemRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class DevelopmentItemUseCase {

    private final DevelopmentItemRepository developmentItemRepository;
    private final CompanyRepository companyRepository;

    public DevelopmentItemResponse add(Long companyId, DevelopmentItemAddRequest request) {

        Company company = companyRepository.findById(companyId).orElseThrow(() -> new DevelopmentItemException(ErrorCode.COMPANY_NOT_FOUND));
        DevelopmentItem developmentItem = developmentItemRepository.save(DevelopmentItem.from(request));
        company.addDevelopmentItem(developmentItem);

        return DevelopmentItemResponse.from(developmentItem);
    }

    @Transactional(readOnly = true)
    public List<DevelopmentItemResponse> getAll(Long companyId, Long loginAccountId) {
        List<DevelopmentItem> developmentItems = developmentItemRepository.findAllByCompanyIdWithAccount(companyId);
        if(!developmentItems.isEmpty()) {
            validateCompanyAccountIdMatch(developmentItems.get(0).getCompany().getAccount().getId(), loginAccountId);
        }
        return developmentItems.stream().map(DevelopmentItemResponse::from).toList();
    }

    @Transactional(readOnly = true)
    public DevelopmentItemResponse get(Long companyId, Long itemId,Long loginAccountId) {
        DevelopmentItem developmentItem = developmentItemRepository.findByIdWithCompanyAndAccount(itemId);

        validateCompanyAccountIdMatch(developmentItem.getCompany().getAccount().getId(), loginAccountId);

        validateCompanyIdMatch(developmentItem.getCompany().getId(), companyId);

        return DevelopmentItemResponse.from(developmentItem);
    }

    private void validateCompanyIdMatch(Long getCompanyId, Long requestCompanyId) {
        if(!getCompanyId.equals(requestCompanyId)) {
            throw new DevelopmentItemException(ErrorCode.ACCESS_DENIED);
        }
    }

    private void validateCompanyAccountIdMatch(Long getAccountId, Long loginAccountId) {
        if(!getAccountId.equals(loginAccountId)) {
            throw new DevelopmentItemException(ErrorCode.ACCESS_DENIED);
        }
    }
}
