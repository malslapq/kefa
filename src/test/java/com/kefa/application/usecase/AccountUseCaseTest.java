package com.kefa.application.usecase;


import com.kefa.api.dto.account.request.AccountDeleteRequest;
import com.kefa.api.dto.account.request.AccountNameUpdateRequest;
import com.kefa.api.dto.account.response.AccountDeleteResponse;
import com.kefa.api.dto.account.response.AccountDetailResponse;
import com.kefa.api.dto.account.response.AccountUpdateResponse;
import com.kefa.common.exception.AccountException;
import com.kefa.common.exception.ErrorCode;
import com.kefa.domain.entity.Account;
import com.kefa.infrastructure.repository.AccountRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
public class AccountUseCaseTest {

    @InjectMocks
    private AccountUseCase accountUseCase;

    @Mock
    private AccountRepository accountRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    private Account account;
    private final Long targetId = 1L;

    @BeforeEach
    void setUp() {
        account = Account.builder()
            .id(1L)
            .email("test@test.com")
            .name("test")
            .password("encodedPassword")
            .build();
    }

    @DisplayName("회원 탈퇴 성공")
    @Test
    void deleteAccountSuccess() {
        // given
        String password = "password123!";
        AccountDeleteRequest request = AccountDeleteRequest.builder()
            .password(password)
            .confirm("DELETE")
            .build();

        given(accountRepository.findById(targetId)).willReturn(Optional.of(account));
        given(passwordEncoder.matches(password, account.getPassword())).willReturn(true);

        // when
        AccountDeleteResponse response = accountUseCase.delete(request, targetId);

        // then
        assertThat(response).isNotNull();
        assertThat(response.getEmail()).isEqualTo(account.getEmail());
        assertThat(response.getMessage()).isEqualTo("계정 탈퇴 성공");
        assertThat(response.getDeletedAt()).isNotNull();
        verify(accountRepository, times(1)).delete(account);
    }

    @DisplayName("회원 탈퇴 실패 - 계정 없음")
    @Test
    void deleteAccountFailAccountNotFound() {
        // given
        AccountDeleteRequest request = AccountDeleteRequest.builder()
            .password("password123!")
            .confirm("DELETE")
            .build();

        given(accountRepository.findById(targetId)).willReturn(Optional.empty());

        // when & then
        assertThatThrownBy(() -> accountUseCase.delete(request, targetId))
            .isInstanceOf(AccountException.class)
            .hasMessage(ErrorCode.NOT_FOUND_ACCOUNT.getMessage());
    }

    @DisplayName("계정 삭제 실패 - 비밀번호 불일치")
    @Test
    void deleteAccountFailWrongPassword() {
        // given
        String wrongPassword = "wrongPassword123!";
        AccountDeleteRequest request = AccountDeleteRequest.builder()
            .password(wrongPassword)
            .confirm("DELETE")
            .build();

        given(accountRepository.findById(targetId)).willReturn(Optional.of(account));
        given(passwordEncoder.matches(wrongPassword, account.getPassword())).willReturn(false);

        // when & then
        assertThatThrownBy(() -> accountUseCase.delete(request, targetId))
            .isInstanceOf(AccountException.class)
            .hasMessage(ErrorCode.INVALID_CREDENTIALS.getMessage());
    }

    @DisplayName("회원 정보 수정 성공 - 본인")
    @Test
    void updateAccountSuccess() {
        // given
        String newName = "updatedName";
        AccountNameUpdateRequest request = new AccountNameUpdateRequest(newName);

        given(accountRepository.findById(targetId)).willReturn(Optional.of(account));

        // when
        AccountUpdateResponse result = accountUseCase.updateAccount(
            request,
            targetId
        );

        // then
        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(targetId);
        assertThat(result.getName()).isEqualTo(newName);
        assertThat(result.getEmail()).isEqualTo(account.getEmail());
        verify(accountRepository, times(1)).findById(targetId);
    }

    @DisplayName("회원 정보 수정 실패 - 계정 없음")
    @Test
    void updateAccountFailAccountNotFound() {
        // given
        AccountNameUpdateRequest request = new AccountNameUpdateRequest("newName");


        given(accountRepository.findById(targetId)).willReturn(Optional.empty());

        // when & then
        assertThatThrownBy(() ->
            accountUseCase.updateAccount(request, targetId)
        )
            .isInstanceOf(AccountException.class)
            .hasMessage(ErrorCode.NOT_FOUND_ACCOUNT.getMessage());
        verify(accountRepository, times(1)).findById(targetId);
    }

    @DisplayName("회원 조회 성공 - 본인")
    @Test
    void findByAccountIdSuccess() {

        //given
        given(accountRepository.findById(targetId)).willReturn(Optional.of(account));

        //when
        AccountDetailResponse result = accountUseCase.findByAccountId(targetId);

        //then
        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(targetId);
        assertThat(result.getEmail()).isEqualTo(account.getEmail());
        assertThat(result.getName()).isEqualTo(account.getName());

    }

    @DisplayName("회원 조회 실패 - 계정 없음")
    @Test
    void findByAccountFailAccountIdNotFound() {

        //given
        given(accountRepository.findById(any())).willReturn(Optional.empty());

        //when & then
        assertThatThrownBy(() -> accountUseCase.findByAccountId(targetId))
            .isInstanceOf(AccountException.class)
            .hasMessage(ErrorCode.NOT_FOUND_ACCOUNT.getMessage());

    }

}