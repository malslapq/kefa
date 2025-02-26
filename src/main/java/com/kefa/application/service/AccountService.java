package com.kefa.application.service;

import com.kefa.api.dto.request.AccountSignupRequestDto;
import com.kefa.api.dto.response.AccountSignupResponseDto;
import com.kefa.application.usecase.AuthenticationUseCase;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AccountService {

    private final AuthenticationUseCase authenticationUseCase;

    public AccountSignupResponseDto signup(AccountSignupRequestDto request) {

        return authenticationUseCase.signup(request);

    }

}
