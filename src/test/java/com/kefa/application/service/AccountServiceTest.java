package com.kefa.application.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.kefa.api.dto.request.account.AccountLoginRequest;
import com.kefa.api.dto.request.account.AccountSignupRequest;
import com.kefa.api.dto.response.account.AccountSignupResponse;
import com.kefa.api.dto.response.account.TokenResponse;
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
    @DisplayName("일반 로그인 성공")
    void login_success(){
        //given
        AccountLoginRequest accountLoginRequest = AccountLoginRequest.builder()
            .email("test@test.com")
            .password("password")
            .deviceId("device1")
            .build();
        TokenResponse tokenResponse = TokenResponse.builder()
            .accessToken("accessToken")
            .refreshToken("refreshToken")
            .build();
        when(authenticationUseCase.login(accountLoginRequest)).thenReturn(tokenResponse);

        //when
        TokenResponse result = accountService.login(accountLoginRequest);

        //then
        assertThat(result).isEqualTo(tokenResponse);
        verify(authenticationUseCase).login(accountLoginRequest);

    }


    @Test
    @DisplayName("일반 회원가입 성공시 이메일 인증메일을 발송한다")
    void signup_success() {
        // given
        AccountSignupRequest request = AccountSignupRequest.builder()
            .email("test@email.com")
            .password("password")
            .name("name")
            .build();

        AccountSignupResponse expectedResponse = AccountSignupResponse.builder()
            .id(1L)
            .email("test@email.com")
            .name("name")
            .build();

        when(authenticationUseCase.signup(request)).thenReturn(expectedResponse);
        doNothing().when(emailVerificationUseCase).sendVerificationEmail(request.getEmail());

        // when
        AccountSignupResponse response = accountService.signup(request);

        // then
        assertThat(response).isEqualTo(expectedResponse);
        verify(authenticationUseCase).signup(request);
        verify(emailVerificationUseCase).sendVerificationEmail(request.getEmail());
    }

    @Test
    @DisplayName("회원가입 실패시 이메일 발송하지 않는다")
    void signup_fail() {
        // given
        AccountSignupRequest request = AccountSignupRequest.builder()
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