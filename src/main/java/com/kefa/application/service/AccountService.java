package com.kefa.application.service;

import com.kefa.api.dto.request.*;
import com.kefa.api.dto.response.*;
import com.kefa.application.usecase.AccountUseCase;
import com.kefa.application.usecase.AuthenticationUseCase;
import com.kefa.application.usecase.EmailVerificationUseCase;
import com.kefa.infrastructure.security.auth.AuthenticationInfo;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class AccountService {

    private final AccountUseCase accountUseCase;
    private final AuthenticationUseCase authenticationUseCase;
    private final EmailVerificationUseCase emailVerificationUseCase;

    public AccountDeleteResponse delete(AccountDeleteRequest accountDeleteRequest, AuthenticationInfo authenticationInfo) {
        return accountUseCase.delete(accountDeleteRequest, authenticationInfo);
    }

    public AccountUpdatePasswordResponseDto updatePassword(AccountUpdatePasswordRequest accountUpdatePasswordRequest, AuthenticationInfo authenticationInfo) {
        return accountUseCase.updatePassword(accountUpdatePasswordRequest, authenticationInfo);
    }

    public AccountUpdateResponse updateAccount(AccountUpdateRequest accountUpdateRequest, AuthenticationInfo authenticationInfo) {
        return accountUseCase.updateAccount(accountUpdateRequest, authenticationInfo);
    }

    public AccountResponse getAccount(AuthenticationInfo authenticationInfo) {
        return accountUseCase.findByAccountId(authenticationInfo);
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
