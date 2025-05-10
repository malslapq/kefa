package com.kefa.application.service;

import com.kefa.api.dto.account.request.*;
import com.kefa.api.dto.account.response.*;
import com.kefa.application.usecase.AccountUseCase;
import com.kefa.application.usecase.AuthenticationUseCase;
import com.kefa.application.usecase.EmailVerificationUseCase;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class AccountService {

    private final AccountUseCase accountUseCase;
    private final AuthenticationUseCase authenticationUseCase;
    private final EmailVerificationUseCase emailVerificationUseCase;

    public SocialInfoDeleteResponse deleteSocialInfo(Long accountId, Long socialInfoId) {
        return accountUseCase.deleteSocialInfo(accountId, socialInfoId);
    }

    public AccountDeleteResponse delete(AccountDeleteRequest accountDeleteRequest, Long loginAccountId) {
        return accountUseCase.delete(accountDeleteRequest, loginAccountId);
    }

    public AccountUpdatePasswordResponse updatePassword(AccountUpdatePasswordRequest accountUpdatePasswordRequest, Long loginAccountId) {
        return accountUseCase.updatePassword(accountUpdatePasswordRequest, loginAccountId);
    }

    public AccountUpdateResponse updateAccount(AccountUpdateRequest accountUpdateRequest, Long loginAccountId) {
        return accountUseCase.updateAccount(accountUpdateRequest, loginAccountId);
    }

    public AccountResponse getAccount(Long loginAccountId) {
        return accountUseCase.findByAccountId(loginAccountId);
    }

    @Transactional
    public AccountSignupResponse signup(AccountSignupRequest request) {

        AccountSignupResponse response = authenticationUseCase.signup(request);
        emailVerificationUseCase.sendVerificationEmail(request.getEmail());

        return response;

    }

    public TokenResponse login(AccountLoginRequest accountLoginRequest) {
        return authenticationUseCase.login(accountLoginRequest);
    }

    public void emailVerify(String token) {
        emailVerificationUseCase.verify(token);
    }

    public void resendVerificationEmail(String email) {
        emailVerificationUseCase.resendEmail(email);
    }
}
