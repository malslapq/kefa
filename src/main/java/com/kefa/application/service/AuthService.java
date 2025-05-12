package com.kefa.application.service;

import com.kefa.api.dto.account.request.AccountLoginRequest;
import com.kefa.api.dto.account.request.AccountSignupRequest;
import com.kefa.api.dto.account.request.AccountUpdatePasswordRequest;
import com.kefa.api.dto.account.response.AccountSignupResponse;
import com.kefa.api.dto.account.response.AccountUpdatePasswordResponse;
import com.kefa.api.dto.account.response.TokenResponse;
import com.kefa.application.usecase.AuthenticationUseCase;
import com.kefa.application.usecase.EmailVerificationUseCase;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final AuthenticationUseCase authenticationUseCase;
    private final EmailVerificationUseCase emailVerificationUseCase;

    public void emailVerify(String token) {
        emailVerificationUseCase.verify(token);
    }

    public void resendVerificationEmail(String email) {
        emailVerificationUseCase.resendEmail(email);
    }

    public AccountUpdatePasswordResponse updatePassword(AccountUpdatePasswordRequest accountUpdatePasswordRequest, Long loginAccountId) {
        return authenticationUseCase.updatePassword(accountUpdatePasswordRequest, loginAccountId);
    }

    public TokenResponse login(AccountLoginRequest accountLoginRequest) {
        return authenticationUseCase.login(accountLoginRequest);
    }

    @Transactional
    public AccountSignupResponse signup(AccountSignupRequest request) {

        AccountSignupResponse response = authenticationUseCase.signup(request);
        emailVerificationUseCase.sendVerificationEmail(request.getEmail());

        return response;

    }

}
