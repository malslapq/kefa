package com.kefa.application.service;

import com.kefa.api.dto.company.response.CompanyResponse;
import com.kefa.api.dto.developmentItem.request.DevelopmentItemAddRequest;
import com.kefa.api.dto.developmentItem.request.DevelopmentItemUpdateCommand;
import com.kefa.api.dto.developmentItem.response.DevelopmentItemResponse;
import com.kefa.application.usecase.CompanyUseCase;
import com.kefa.application.usecase.DevelopmentItemUseCase;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class DevelopmentItemService {

    private final CompanyUseCase companyUseCase;
    private final DevelopmentItemUseCase developmentItemUseCase;

    @Transactional
    public void delete(Long companyId, Long itemId, Long loginAccountId) {
        companyUseCase.getMyCompany(companyId, loginAccountId);
        developmentItemUseCase.delete(itemId, loginAccountId);
    }

    @Transactional
    public DevelopmentItemResponse update(DevelopmentItemUpdateCommand command) {
        companyUseCase.getMyCompany(command.getCompanyId(), command.getAccountId());
        return developmentItemUseCase.update(command);
    }

    @Transactional
    public DevelopmentItemResponse add(Long companyId, DevelopmentItemAddRequest request, Long loginAccountId) {
        CompanyResponse myCompany = companyUseCase.getMyCompany(companyId, loginAccountId);
        return developmentItemUseCase.add(myCompany.getId(), request);
    }

    public List<DevelopmentItemResponse> getAll(Long companyId, Long loginAccountId) {
        return developmentItemUseCase.getAll(companyId, loginAccountId);
    }

    public DevelopmentItemResponse get(Long companyId, Long itemId,Long loginAccountId) {
        return developmentItemUseCase.get(companyId, itemId,loginAccountId);
    }
}
