package com.kefa.application.service;

import com.kefa.api.dto.account.command.GetAccountsCommand;
import com.kefa.api.dto.account.response.AccountDetailResponse;
import com.kefa.api.dto.consulting.response.PagedResponse;
import com.kefa.application.usecase.AdminUseCase;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AdminService {

    private final AdminUseCase adminUseCase;

    public PagedResponse<AccountDetailResponse> getAccounts(GetAccountsCommand command) {
        return adminUseCase.getAccounts(command);
    }
}
