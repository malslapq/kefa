package com.kefa.application.service;

import com.kefa.api.dto.AccountUpdateSubscriptionTypeRequest;
import com.kefa.api.dto.account.command.GetAccountsCommand;
import com.kefa.api.dto.account.request.AccountUpdatePasswordRequestFromAdmin;
import com.kefa.api.dto.account.request.AccountUpdateRequest;
import com.kefa.api.dto.account.request.AccountUpdateRoleRequest;
import com.kefa.api.dto.account.response.AccountDetailResponse;
import com.kefa.api.dto.consulting.response.PagedResponse;
import com.kefa.application.usecase.AdminUseCase;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AdminService {

    private final AdminUseCase adminUseCase;

    public void deleteAccountSocialInfo(Long accountId) {
        adminUseCase.deleteAccountSocialInfo(accountId);
    }

    public void deleteAccount(Long accountId) {
        adminUseCase.deleteAccount(accountId);
    }

    public AccountDetailResponse updateAccountSubscriptionType(Long accountId, AccountUpdateSubscriptionTypeRequest request) {
        return adminUseCase.updateAccountSubscriptionType(accountId, request);
    }

    public AccountDetailResponse updateAccountRole(Long accountId, AccountUpdateRoleRequest request) {
        return adminUseCase.updateAccountRole(accountId, request);
    }

    public AccountDetailResponse updateAccountPassword(Long accountId, AccountUpdatePasswordRequestFromAdmin request) {
        return adminUseCase.updateAccountPassword(accountId, request);
    }

    public AccountDetailResponse updateAccount(Long accountId, AccountUpdateRequest request) {
        return adminUseCase.updateAccount(accountId, request);
    }

    public PagedResponse<AccountDetailResponse> getAccounts(GetAccountsCommand command) {
        return adminUseCase.getAccounts(command);
    }
}
