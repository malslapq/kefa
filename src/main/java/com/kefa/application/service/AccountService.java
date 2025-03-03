package com.kefa.application.service;

import com.kefa.api.dto.AccountUpdateRequestDto;
import com.kefa.api.dto.request.AccountLoginRequestDto;
import com.kefa.api.dto.request.AccountSignupRequestDto;
import com.kefa.api.dto.response.AccountResponseDto;
import com.kefa.api.dto.response.AccountSignupResponseDto;
import com.kefa.api.dto.response.AccountUpdateResponseDto;
import com.kefa.api.dto.response.TokenResponse;
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

    public AccountUpdateResponseDto updateAccount(Long targetId, AccountUpdateRequestDto accountUpdateRequestDto, AuthenticationInfo authenticationInfo) {
        return accountUseCase.updateAccount(targetId, accountUpdateRequestDto, authenticationInfo);
    }

    public AccountResponseDto getAccount(Long targetId, AuthenticationInfo authenticationInfo) {
        return accountUseCase.getAccount(targetId, authenticationInfo);
    }

    @Transactional
    public AccountSignupResponseDto signup(AccountSignupRequestDto request) {

        AccountSignupResponseDto response = authenticationUseCase.signup(request);
        emailVerificationUseCase.sendVerificationEmail(request.getEmail());

        return response;

    }

    public TokenResponse login(AccountLoginRequestDto accountLoginRequestDto) {
        return authenticationUseCase.login(accountLoginRequestDto);
    }

    public void emailVerify(String token) {
        emailVerificationUseCase.verify(token);
    }

    public void resendVerificationEmail(String email) {
        emailVerificationUseCase.resendEmail(email);
    }
}
