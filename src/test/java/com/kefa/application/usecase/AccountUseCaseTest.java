package com.kefa.application.usecase;


import com.kefa.api.dto.request.AccountDeleteRequest;
import com.kefa.api.dto.request.AccountUpdateRequest;
import com.kefa.api.dto.request.AccountUpdatePasswordRequest;
import com.kefa.api.dto.response.AccountDeleteResponse;
import com.kefa.api.dto.response.AccountResponse;
import com.kefa.api.dto.response.AccountUpdateResponse;
import com.kefa.api.dto.response.AccountUpdatePasswordResponseDto;
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
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class AccountUseCaseTest {

    @InjectMocks
    private AccountUseCase accountUseCase;

    @Mock
    private AccountRepository accountRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    private Account account;

    @BeforeEach
    void setUp() {
        account = Account.builder()
            .id(1L)
            .email("test@test.com")
            .name("test")
            .password("encodedPassword")
            .build();
    }

    @DisplayName("계정 삭제 성공")
    @Test
    void deleteAccountSuccess() {
        // given
        Long targetId = 1L;
        String password = "password123!";
        AccountDeleteRequest request = AccountDeleteRequest.builder()
            .password(password)
            .confirm("DELETE")
            .build();
        AuthenticationInfo authInfo = AuthenticationInfo.builder()
            .id(targetId)
            .build();

        given(accountRepository.findById(targetId)).willReturn(Optional.of(account));
        given(passwordEncoder.matches(password, account.getPassword())).willReturn(true);

        // when
        AccountDeleteResponse response = accountUseCase.delete(request, authInfo);

        // then
        assertThat(response).isNotNull();
        assertThat(response.getEmail()).isEqualTo(account.getEmail());
        assertThat(response.getMessage()).isEqualTo("계정 탈퇴 성공");
        assertThat(response.getDeletedAt()).isNotNull();
    }

    @DisplayName("계정 삭제 실패 - 계정 없음")
    @Test
    void deleteAccountFailAccountNotFound() {
        // given
        Long targetId = 1L;
        AccountDeleteRequest request = AccountDeleteRequest.builder()
            .password("password123!")
            .confirm("DELETE")
            .build();
        AuthenticationInfo authInfo = AuthenticationInfo.builder()
            .id(targetId)
            .build();

        given(accountRepository.findById(targetId)).willReturn(Optional.empty());

        // when & then
        assertThatThrownBy(() ->
            accountUseCase.delete(request, authInfo)
        )
            .isInstanceOf(AuthenticationException.class)
            .hasMessage(ErrorCode.ACCOUNT_NOT_FOUND.getMessage());
    }

    @DisplayName("계정 삭제 실패 - 비밀번호 불일치")
    @Test
    void deleteAccountFailWrongPassword() {
        // given
        Long targetId = 1L;
        String wrongPassword = "wrongPassword123!";
        AccountDeleteRequest request = AccountDeleteRequest.builder()
            .password(wrongPassword)
            .confirm("DELETE")
            .build();
        AuthenticationInfo authInfo = AuthenticationInfo.builder()
            .id(targetId)
            .build();

        given(accountRepository.findById(targetId)).willReturn(Optional.of(account));
        given(passwordEncoder.matches(wrongPassword, account.getPassword())).willReturn(false);

        // when & then
        assertThatThrownBy(() ->
            accountUseCase.delete(request, authInfo)
        )
            .isInstanceOf(AuthenticationException.class)
            .hasMessage(ErrorCode.INVALID_CREDENTIALS.getMessage());
    }

    @DisplayName("비밀번호 변경 성공")
    @Test
    void updatePasswordSuccess() {
        // given
        Long targetId = 1L;
        String prevPassword = "prevPass123!";
        String newPassword = "newPass456@";
        String encodedNewPassword = "encodedNewPassword";
        AccountUpdatePasswordRequest request = AccountUpdatePasswordRequest.builder()
            .prevPassword(prevPassword)
            .newPassword(newPassword)
            .build();
        AuthenticationInfo authInfo = AuthenticationInfo.builder()
            .id(targetId)
            .build();

        given(accountRepository.findById(targetId)).willReturn(Optional.of(account));
        given(passwordEncoder.matches(prevPassword, account.getPassword())).willReturn(true);
        given(passwordEncoder.encode(newPassword)).willReturn(encodedNewPassword);

        // when
        AccountUpdatePasswordResponseDto response = accountUseCase.updatePassword(request, authInfo);

        // then
        assertThat(account.getPassword()).isEqualTo(encodedNewPassword);
        assertThat(response).isNotNull();
        assertThat(response.getMessage()).isEqualTo("비밀번호 변경 완료");
        assertThat(response.getUpdateAt()).isNotNull();

    }

    @DisplayName("비밀번호 변경 실패 - 비밀번호 틀림")
    @Test
    void updatePasswordFailWrongPassword() {
        // given
        Long targetId = 1L;
        AccountUpdatePasswordRequest request = AccountUpdatePasswordRequest.builder()
            .prevPassword("wrongPass123!")
            .newPassword("newPass456@")
            .build();
        AuthenticationInfo authInfo = AuthenticationInfo.builder()
            .id(targetId)
            .build();

        given(accountRepository.findById(targetId)).willReturn(Optional.of(account));
        given(passwordEncoder.matches(request.getPrevPassword(), account.getPassword())).willReturn(false);

        // when & then
        assertThatThrownBy(() ->
            accountUseCase.updatePassword(request, authInfo)
        )
            .isInstanceOf(AuthenticationException.class)
            .hasMessage(ErrorCode.INVALID_CREDENTIALS.getMessage());
    }

    @DisplayName("비밀번호 변경 실패 - 새 비밀번호가 현재와 동일")
    @Test
    void updatePasswordFailSamePassword() {
        // given
        Long targetId = 1L;
        String password = "samePass123!";
        AccountUpdatePasswordRequest request = AccountUpdatePasswordRequest.builder()
            .prevPassword(password)
            .newPassword(password)
            .build();
        AuthenticationInfo authInfo = AuthenticationInfo.builder()
            .id(targetId)
            .build();

        given(accountRepository.findById(targetId)).willReturn(Optional.of(account));
        given(passwordEncoder.matches(password, account.getPassword())).willReturn(true);

        // when & then
        assertThatThrownBy(() ->
            accountUseCase.updatePassword(request, authInfo)
        )
            .isInstanceOf(AuthenticationException.class)
            .hasMessage(ErrorCode.NEW_PASSWORD_MUST_BE_DIFFERENT.getMessage());
    }

    @DisplayName("비밀번호 변경 실패 - 계정 없음")
    @Test
    void updatePasswordFailAccountNotFound() {
        // given
        Long targetId = 1L;
        AccountUpdatePasswordRequest request = AccountUpdatePasswordRequest.builder()
            .prevPassword("prevPass123!")
            .newPassword("newPass456@")
            .build();
        AuthenticationInfo authInfo = AuthenticationInfo.builder()
            .id(targetId)
            .build();
        given(accountRepository.findById(targetId)).willReturn(Optional.empty());

        // when & then
        assertThatThrownBy(() ->
            accountUseCase.updatePassword(request, authInfo)
        )
            .isInstanceOf(AuthenticationException.class)
            .hasMessage(ErrorCode.ACCOUNT_NOT_FOUND.getMessage());
    }

    @DisplayName("회원 정보 수정 성공 - 본인")
    @Test
    void updateAccountSuccess() {
        // given
        Long targetId = 1L;
        String newName = "updatedName";
        AccountUpdateRequest request = new AccountUpdateRequest(newName);
        AuthenticationInfo authInfo = AuthenticationInfo.builder()
            .id(targetId)
            .build();

        given(accountRepository.findById(targetId)).willReturn(Optional.of(account));

        // when
        AccountUpdateResponse result = accountUseCase.updateAccount(
            request,
            authInfo
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
        Long targetId = 1L;
        AccountUpdateRequest request = new AccountUpdateRequest("newName");
        AuthenticationInfo authInfo = AuthenticationInfo.builder()
            .id(targetId)
            .build();

        given(accountRepository.findById(targetId)).willReturn(Optional.empty());

        // when & then
        assertThatThrownBy(() ->
            accountUseCase.updateAccount(request, authInfo)
        )
            .isInstanceOf(AuthenticationException.class)
            .hasMessage(ErrorCode.ACCOUNT_NOT_FOUND.getMessage());
        verify(accountRepository, times(1)).findById(targetId);
    }

    @DisplayName("회원 조회 성공 - 본인")
    @Test
    void findByAccountIdSuccess() {

        //given
        Long targetId = 1L;
        AuthenticationInfo authenticationInfo = AuthenticationInfo.builder()
            .id(targetId)
            .build();
        given(accountRepository.findById(targetId)).willReturn(Optional.of(account));

        //when
        AccountResponse result = accountUseCase.findByAccountId(authenticationInfo);

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
        Long targetId = 1L;
        AuthenticationInfo authenticationInfo = AuthenticationInfo.builder()
            .id(targetId)
            .build();
        given(accountRepository.findById(any())).willReturn(Optional.empty());

        //when & then
        assertThatThrownBy(() -> accountUseCase.findByAccountId(authenticationInfo))
            .isInstanceOf(AuthenticationException.class)
            .hasMessage(ErrorCode.ACCOUNT_NOT_FOUND.getMessage());

    }

}