package com.kefa.application.usecase;

import com.kefa.api.dto.request.AccountSignupRequestDto;
import com.kefa.api.dto.response.AccountSignupResponseDto;
import com.kefa.common.exception.AuthenticationException;
import com.kefa.common.exception.ErrorCode;
import com.kefa.domain.entity.Account;
import com.kefa.domain.type.LoginType;
import com.kefa.domain.type.Role;
import com.kefa.domain.type.SubscriptionType;
import com.kefa.domain.vo.AccountVO;
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
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuthenticationUseCaseTest {

    @Mock
    private AccountRepository accountRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private AuthenticationUseCase authenticationUseCase;

    private AccountSignupRequestDto requestDto;

    @BeforeEach
    void setUp() {
        requestDto = AccountSignupRequestDto.builder()
            .email("test@example.com")
            .password("password123")
            .build();
    }

    @Test
    @DisplayName("일반 회원가입 성공")
    void signupSuccess() {
        // given
        Account account = Account.builder()
            .id(1L)
            .email("test@example.com")
            .name("name")
            .password("encodedPassword")
            .subscriptionType(SubscriptionType.FREE)
            .role(Role.ACCOUNT)
            .verified(false)
            .loginTypes(Set.of(LoginType.LOCAL))
            .build();

        when(accountRepository.existsByEmail(requestDto.getEmail())).thenReturn(false);
        when(passwordEncoder.encode(requestDto.getPassword())).thenReturn("encodedPassword");
        when(accountRepository.save(any(Account.class))).thenReturn(account);

        // when
        AccountSignupResponseDto responseDto = authenticationUseCase.signup(requestDto);

        // then
        assertThat(responseDto.getEmail()).isEqualTo(requestDto.getEmail());
        verify(accountRepository).save(argThat(savedAccount ->
            savedAccount.getEmail().equals(requestDto.getEmail()) &&
                savedAccount.getLoginTypes().contains(LoginType.LOCAL) &&
                savedAccount.getRole() == Role.ACCOUNT &&
                !savedAccount.isVerified() &&
                savedAccount.getSubscriptionType() == SubscriptionType.FREE
        ));
    }

    @Test
    @DisplayName("중복 이메일로 인한 회원가입 실패")
    void signupDuplicateEmailFailure() {
        // given
        when(accountRepository.existsByEmail(requestDto.getEmail())).thenReturn(true);

        // when & then
        assertThatThrownBy(() -> authenticationUseCase.signup(requestDto))
            .isInstanceOf(AuthenticationException.class)
            .hasMessageContaining(ErrorCode.DUPLICATE_EMAIL.getMessage());
    }

    @Test
    @DisplayName("소셜 로그인 성공, 신규 사용자 계정 생성")
    void Oauth2JoinAndLoginSuccess() {
        // given
        Account newAccount = Account.builder()
            .id(1L)
            .email("test@example.com")
            .name("name")
            .subscriptionType(SubscriptionType.FREE)
            .role(Role.ACCOUNT)
            .verified(true)
            .loginTypes(Set.of(LoginType.GOOGLE))
            .build();

        when(accountRepository.findByEmail("test@example.com")).thenReturn(Optional.empty());
        when(accountRepository.save(any(Account.class))).thenReturn(newAccount);

        // when
        AccountVO accountVO = authenticationUseCase.authenticateSocialUser("test@example.com", "google");

        // then
        assertThat(accountVO.getEmail()).isEqualTo("test@example.com");
        verify(accountRepository).save(argThat(savedAccount ->
            savedAccount.getEmail().equals("test@example.com") &&
                savedAccount.getLoginTypes().contains(LoginType.GOOGLE) &&
                savedAccount.getRole() == Role.ACCOUNT &&
                savedAccount.isVerified() &&
                savedAccount.getSubscriptionType() == SubscriptionType.FREE
        ));
    }

    @Test
    @DisplayName("소셜 로그인 성공, 기존 사용자는 저장하지 않음")
    void Oauth2LoginSuccess() {
        // given
        Account existingAccount = Account.builder()
            .id(1L)
            .email("test@example.com")
            .name("name")
            .subscriptionType(SubscriptionType.FREE)
            .role(Role.ACCOUNT)
            .verified(true)
            .loginTypes(Set.of(LoginType.GOOGLE))
            .build();

        when(accountRepository.findByEmail("test@example.com"))
            .thenReturn(Optional.of(existingAccount));

        // when
        AccountVO accountVO = authenticationUseCase.authenticateSocialUser("test@example.com", "google");

        // then
        assertThat(accountVO.getEmail()).isEqualTo("test@example.com");
        assertThat(existingAccount.getLoginTypes()).contains(LoginType.GOOGLE);
        assertThat(existingAccount.isVerified()).isEqualTo(true);
        verify(accountRepository, never()).save(any());
    }

    @Test
    @DisplayName("지원하지 않는 소셜 로그인 시도 실패")
    void unsupportedOAuth2ProviderLoginFailed() {
        // when & then
        assertThatThrownBy(() ->
            authenticationUseCase.authenticateSocialUser("test@example.com", "unknown"))
            .isInstanceOf(AuthenticationException.class)
            .hasMessageContaining(ErrorCode.UNSUPPORTED_SOCIAL_PROVIDER.getMessage());
    }
}