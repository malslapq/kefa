package com.kefa.application.service;

import com.kefa.api.dto.account.request.AccountDeleteRequest;
import com.kefa.api.dto.account.request.AccountNameUpdateRequest;
import com.kefa.api.dto.account.response.AccountDeleteResponse;
import com.kefa.api.dto.account.response.AccountDetailResponse;
import com.kefa.api.dto.account.response.AccountUpdateResponse;
import com.kefa.api.dto.account.response.SocialInfoDeleteResponse;
import com.kefa.application.usecase.AccountUseCase;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AccountService {

    private final AccountUseCase accountUseCase;

    public SocialInfoDeleteResponse deleteSocialInfo(Long accountId, Long socialInfoId) {
        return accountUseCase.deleteSocialInfo(accountId, socialInfoId);
    }

    public AccountDeleteResponse delete(AccountDeleteRequest accountDeleteRequest, Long loginAccountId) {
        return accountUseCase.delete(accountDeleteRequest, loginAccountId);
    }

    public AccountUpdateResponse updateAccount(AccountNameUpdateRequest accountNameUpdateRequest, Long loginAccountId) {
        return accountUseCase.updateAccount(accountNameUpdateRequest, loginAccountId);
    }

    public AccountDetailResponse getAccount(Long loginAccountId) {
        return accountUseCase.findByAccountId(loginAccountId);
    }

}
