package com.kefa.application.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.kefa.api.dto.request.AccountSignupRequestDto;
import com.kefa.api.dto.response.AccountSignupResponseDto;
import com.kefa.application.usecase.AuthenticationUseCase;
import com.kefa.application.usecase.EmailVerificationUseCase;
import com.kefa.common.exception.AuthenticationException;
import com.kefa.common.exception.ErrorCode;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
public class AccountServiceTest {

    @Mock
    private AuthenticationUseCase authenticationUseCase;

    @Mock
    private EmailVerificationUseCase emailVerificationUseCase;

    @InjectMocks
    private AccountService accountService;

    @Test
    @DisplayName("일반 회원가입 성공시 이메일 인증메일을 발송한다")
    void signup_success() {
        // given
        AccountSignupRequestDto request = AccountSignupRequestDto.builder()
            .email("test@email.com")
            .password("password")
            .name("name")
            .build();

        AccountSignupResponseDto expectedResponse = AccountSignupResponseDto.builder()
            .id(1L)
            .email("test@email.com")
            .name("name")
            .build();

        when(authenticationUseCase.signup(request)).thenReturn(expectedResponse);
        doNothing().when(emailVerificationUseCase).sendVerificationEmail(request.getEmail());

        // when
        AccountSignupResponseDto response = accountService.signup(request);

        // then
        assertThat(response).isEqualTo(expectedResponse);
        verify(authenticationUseCase).signup(request);
        verify(emailVerificationUseCase).sendVerificationEmail(request.getEmail());
    }

    @Test
    @DisplayName("회원가입 실패시 이메일 발송하지 않는다")
    void signup_fail() {
        // given
        AccountSignupRequestDto request = AccountSignupRequestDto.builder()
            .email("test@email.com")
            .password("password")
            .name("name")
            .build();

        when(authenticationUseCase.signup(request))
            .thenThrow(new AuthenticationException(ErrorCode.DUPLICATE_EMAIL));

        // when & then
        assertThatThrownBy(() -> accountService.signup(request))
            .isInstanceOf(AuthenticationException.class);

        verify(authenticationUseCase).signup(request);
        verify(emailVerificationUseCase, never()).sendVerificationEmail(any());
    }
}