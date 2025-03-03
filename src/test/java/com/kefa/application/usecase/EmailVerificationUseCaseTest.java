package com.kefa.application.usecase;

import com.kefa.common.exception.AuthenticationException;
import com.kefa.common.exception.ErrorCode;
import com.kefa.domain.entity.Account;
import com.kefa.infrastructure.mail.EmailSender;
import com.kefa.infrastructure.repository.AccountRepository;
import com.kefa.infrastructure.repository.EmailVerificationRedisRepository;
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
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class EmailVerificationUseCaseTest {

    private String testEmail = "test@email.com";
    private String testToken = "testToken";
    private String wrongToken = "wrongToken";

    @Mock
    private AccountRepository accountRepository;

    @Mock
    private EmailVerificationRedisRepository emailVerificationRedisRepository;

    @Mock
    private EmailSender emailSender;

    @InjectMocks
    private EmailVerificationUseCase emailVerificationUseCase;

    private Account createAccount(boolean verified) {
        return Account.builder()
            .email(testEmail)
            .emailVerified(verified)
            .build();
    }

    @Test
    @DisplayName("이메일 인증 재발송 성공")
    void resendVerificationEmail_success() {
        // given
        Account account = createAccount(false);

        when(accountRepository.findByEmail(testEmail)).thenReturn(Optional.of(account));
        doNothing().when(emailVerificationRedisRepository).saveEmailToken(any(), eq(testEmail));
        doNothing().when(emailSender).sendVerificationEmail(eq(testEmail), any());

        // when
        emailVerificationUseCase.resendEmail(testEmail);

        // then
        verify(emailVerificationRedisRepository).saveEmailToken(any(), eq(testEmail));
        verify(emailSender).sendVerificationEmail(eq(testEmail), any());
    }

    @Test
    @DisplayName("존재하지 않는 계정으로 이메일 재발송 시도시 실패")
    void resendVerificationEmail_failWithNonExistentAccount() {
        // given
        when(accountRepository.findByEmail(testEmail)).thenReturn(Optional.empty());

        // when & then
        assertThatThrownBy(() -> emailVerificationUseCase.resendEmail(testEmail))
            .isInstanceOf(AuthenticationException.class)
            .hasFieldOrPropertyWithValue("errorCode", ErrorCode.ACCOUNT_NOT_FOUND);

        verify(emailVerificationRedisRepository, never()).saveEmailToken(any(), any());
        verify(emailSender, never()).sendVerificationEmail(any(), any());
    }

    @Test
    @DisplayName("이미 인증된 이메일로 재발송 시도시 실패")
    void resendVerificationEmail_failWithAlreadyVerifiedEmail() {
        // given
        Account account = createAccount(true);

        when(accountRepository.findByEmail(testEmail)).thenReturn(Optional.of(account));

        // when & then
        assertThatThrownBy(() -> emailVerificationUseCase.resendEmail(testEmail))
            .isInstanceOf(AuthenticationException.class)
            .hasFieldOrPropertyWithValue("errorCode", ErrorCode.ALREADY_VERIFIED_EMAIL);

        verify(emailVerificationRedisRepository, never()).saveEmailToken(any(), any());
        verify(emailSender, never()).sendVerificationEmail(any(), any());
    }

    @Test
    @DisplayName("이메일 인증 성공")
    void verify_success() {
        // given
        Account account = createAccount(false);

        when(emailVerificationRedisRepository.getEmailByToken(testToken)).thenReturn(testEmail);
        when(accountRepository.findByEmail(testEmail)).thenReturn(Optional.of(account));

        // when
        emailVerificationUseCase.verify(testToken);

        // then
        assertThat(account.isEmailVerified()).isTrue();
        verify(emailVerificationRedisRepository).removeEmailToken(testToken);
    }

    @Test
    @DisplayName("유효하지 않은 토큰으로 인증 시도시 실패")
    void verify_failWithInvalidToken() {
        // given
        when(emailVerificationRedisRepository.getEmailByToken(wrongToken)).thenReturn(null);

        // when & then
        assertThatThrownBy(() -> emailVerificationUseCase.verify(wrongToken))
            .isInstanceOf(AuthenticationException.class)
            .hasFieldOrPropertyWithValue("errorCode", ErrorCode.INVALID_EMAIL_VERIFICATION_TOKEN);

        verify(accountRepository, never()).findByEmail(any());
        verify(emailVerificationRedisRepository, never()).removeEmailToken(any());
    }

    @Test
    @DisplayName("존재하지 않는 계정으로 인증 시도시 실패")
    void verify_failWithNonExistentAccount() {
        // given
        when(emailVerificationRedisRepository.getEmailByToken(testToken)).thenReturn(testEmail);
        when(accountRepository.findByEmail(testEmail)).thenReturn(Optional.empty());

        // when & then
        assertThatThrownBy(() -> emailVerificationUseCase.verify(testToken))
            .isInstanceOf(AuthenticationException.class)
            .hasFieldOrPropertyWithValue("errorCode", ErrorCode.ACCOUNT_NOT_FOUND);

        verify(emailVerificationRedisRepository, never()).removeEmailToken(any());
    }

    @Test
    @DisplayName("이메일 인증 메일 발송 성공")
    void sendVerificationEmail_success() {
        // given
        doNothing().when(emailVerificationRedisRepository).saveEmailToken(any(), eq(testEmail));
        doNothing().when(emailSender).sendVerificationEmail(eq(testEmail), any());

        // when
        emailVerificationUseCase.sendVerificationEmail(testEmail);

        // then
        verify(emailVerificationRedisRepository).saveEmailToken(any(), eq(testEmail));
        verify(emailSender).sendVerificationEmail(eq(testEmail), any());
    }

    @Test
    @DisplayName("Redis 저장 실패시 이메일 발송하지 않음")
    void sendVerificationEmail_failOnRedis() {
        // given
        doThrow(new RuntimeException()).when(emailVerificationRedisRepository).saveEmailToken(any(), eq(testEmail));

        // when & then
        assertThatThrownBy(() -> emailVerificationUseCase.sendVerificationEmail(testEmail))
            .isInstanceOf(RuntimeException.class);

        verify(emailVerificationRedisRepository).saveEmailToken(any(), eq(testEmail));
        verify(emailSender, never()).sendVerificationEmail(any(), any());
    }
}