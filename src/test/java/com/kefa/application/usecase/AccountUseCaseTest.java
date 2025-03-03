package com.kefa.application.usecase;


import com.kefa.api.dto.response.AccountResponseDto;
import com.kefa.common.exception.AuthenticationException;
import com.kefa.common.exception.ErrorCode;
import com.kefa.domain.entity.Account;
import com.kefa.infrastructure.repository.AccountRepository;
import com.kefa.infrastructure.security.auth.AuthenticationInfo;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;

@ExtendWith(MockitoExtension.class)
public class AccountUseCaseTest {

    @InjectMocks
    private AccountUseCase accountUseCase;

    @Mock
    private AccountRepository accountRepository;

    private Account account;

    @BeforeEach
    void setUp() {
        account = Account.builder()
            .id(1L)
            .email("test@test.com")
            .name("test")
            .build();
    }

    @DisplayName("회원 조회 성공 - 본인")
    @Test
    void getAccountSuccess() {

        //given
        Long targetId = 1L;
        AuthenticationInfo authenticationInfo = AuthenticationInfo.builder()
            .id(targetId)
            .build();
        given(accountRepository.findById(targetId)).willReturn(Optional.of(account));

        //when
        AccountResponseDto result = accountUseCase.getAccount(targetId, authenticationInfo);

        //then
        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(targetId);
        assertThat(result.getEmail()).isEqualTo(account.getEmail());
        assertThat(result.getName()).isEqualTo(account.getName());

    }

    @DisplayName("")
    @Test
    void getAccountFai_AccessDenied() {

        //given
        Long targetId = 2L;
        AuthenticationInfo authenticationInfo = AuthenticationInfo.builder()
            .id(account.getId())
            .build();

        //when & then
        assertThatThrownBy(() -> accountUseCase.getAccount(targetId, authenticationInfo))
            .isInstanceOf(AuthenticationException.class)
            .hasMessage(ErrorCode.ACCESS_DENIED.getMessage());

    }

    @DisplayName("")
    @Test
    void getAccountFailAccountNotFound() {

        //given
        Long targetId = 1L;
        AuthenticationInfo authenticationInfo = AuthenticationInfo.builder()
            .id(targetId)
            .build();
        given(accountRepository.findById(any())).willReturn(Optional.empty());

        //when & then
        assertThatThrownBy(() -> accountUseCase.getAccount(targetId, authenticationInfo))
            .isInstanceOf(AuthenticationException.class)
            .hasMessage(ErrorCode.ACCOUNT_NOT_FOUND.getMessage());

    }


}