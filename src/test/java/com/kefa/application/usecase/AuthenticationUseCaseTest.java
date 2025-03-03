package com.kefa.application.usecase;

import com.kefa.api.dto.request.AccountLoginRequestDto;
import com.kefa.api.dto.request.AccountSignupRequestDto;
import com.kefa.api.dto.response.AccountSignupResponseDto;
import com.kefa.api.dto.response.TokenResponse;
import com.kefa.common.exception.AuthenticationException;
import com.kefa.common.exception.ErrorCode;
import com.kefa.domain.entity.Account;
import com.kefa.domain.type.LoginType;
import com.kefa.domain.type.Role;
import com.kefa.domain.type.SubscriptionType;
import com.kefa.domain.vo.AccountVO;
import com.kefa.infrastructure.repository.AccountRepository;
import com.kefa.infrastructure.repository.RefreshTokenRepository;
import com.kefa.infrastructure.security.jwt.JwtProvider;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.LocalDateTime;
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

    @Mock
    private RefreshTokenRepository refreshTokenRepository;

    @Mock
    private JwtProvider jwtProvider;

    @InjectMocks
    private AuthenticationUseCase authenticationUseCase;

    private AccountSignupRequestDto signupRequestDto;
    private AccountLoginRequestDto loginRequest;


    @BeforeEach
    void setUp() {
        signupRequestDto = AccountSignupRequestDto.builder()
            .email("test@example.com")
            .password("password123")
            .build();

        loginRequest = AccountLoginRequestDto.builder()
            .email("test@test.com")
            .password("password")
            .deviceId("device1")
            .build();
    }

    @Test
    @DisplayName("로그인 성공")
    void loginSuccess() {
        // given
        Account account = Account.builder()
            .id(1L)
            .email("test@example.com")
            .password("encodedPassword")
            .name("name")
            .subscriptionType(SubscriptionType.FREE)
            .role(Role.ACCOUNT)
            .emailVerified(true)
            .loginTypes(Set.of(LoginType.LOCAL))
            .build();

        String accessToken = "accessToken";
        String refreshToken = "refreshToken";
        LocalDateTime expirationTime = LocalDateTime.now().plusDays(2);

        when(accountRepository.findByEmail(loginRequest.getEmail())).thenReturn(Optional.of(account));
        when(passwordEncoder.matches(loginRequest.getPassword(), account.getPassword())).thenReturn(true);
        when(jwtProvider.createAccessToken(account.getId(), account.getRole())).thenReturn(accessToken, refreshToken);
        when(jwtProvider.getTokenExpiration(refreshToken)).thenReturn(expirationTime);

        // when
        TokenResponse response = authenticationUseCase.login(loginRequest);

        // then
        assertThat(response.getAccessToken()).isEqualTo(accessToken);
        assertThat(response.getRefreshToken()).isEqualTo(refreshToken);
        verify(refreshTokenRepository).save(argThat(savedToken ->
            savedToken.getToken().equals(refreshToken) &&
                savedToken.getDeviceId().equals(loginRequest.getDeviceId()) &&
                savedToken.getAccountId().equals(account.getId()) &&
                savedToken.getExpiresAt().equals(expirationTime)
        ));
    }

    @Test
    @DisplayName("존재하지 않는 이메일로 로그인 실패")
    void loginFailWhenEmailNotFound() {
        // given
        AccountLoginRequestDto loginRequest = AccountLoginRequestDto.builder()
            .email("empty@example.com")
            .password("password123")
            .deviceId("device1")
            .build();

        when(accountRepository.findByEmail(loginRequest.getEmail())).thenReturn(Optional.empty());

        // when & then
        assertThatThrownBy(() -> authenticationUseCase.login(loginRequest))
            .isInstanceOf(AuthenticationException.class)
            .hasMessageContaining(ErrorCode.INVALID_CREDENTIALS.getMessage());
    }

    @Test
    @DisplayName("비밀번호 다름으로 인한 로그인 실패")
    void loginFailWhenPasswordWrong() {
        // given
        AccountLoginRequestDto loginRequest = AccountLoginRequestDto.builder()
            .email("test@example.com")
            .password("wrongPassword")
            .deviceId("device1")
            .build();

        Account account = Account.builder()
            .id(1L)
            .email("test@example.com")
            .password("encodedPassword")
            .name("name")
            .subscriptionType(SubscriptionType.FREE)
            .role(Role.ACCOUNT)
            .emailVerified(true)
            .loginTypes(Set.of(LoginType.LOCAL))
            .build();

        when(accountRepository.findByEmail(loginRequest.getEmail())).thenReturn(Optional.of(account));
        when(passwordEncoder.matches(loginRequest.getPassword(), account.getPassword())).thenReturn(false);

        // when & then
        assertThatThrownBy(() -> authenticationUseCase.login(loginRequest))
            .isInstanceOf(AuthenticationException.class)
            .hasMessageContaining(ErrorCode.INVALID_CREDENTIALS.getMessage());
    }

    @Test
    @DisplayName("이메일 미인증 계정으로 로그인 시도시 실패")
    void loginFailWhenAccountNotVerified() {
        // given
        AccountLoginRequestDto loginRequest = AccountLoginRequestDto.builder()
            .email("test@example.com")
            .password("password123")
            .deviceId("device1")
            .build();

        Account account = Account.builder()
            .id(1L)
            .email("test@example.com")
            .password("encodedPassword")
            .name("name")
            .subscriptionType(SubscriptionType.FREE)
            .role(Role.ACCOUNT)
            .emailVerified(false)  // 미인증 계정
            .loginTypes(Set.of(LoginType.LOCAL))
            .build();

        when(accountRepository.findByEmail(loginRequest.getEmail())).thenReturn(Optional.of(account));
        when(passwordEncoder.matches(loginRequest.getPassword(), account.getPassword())).thenReturn(true);

        // when & then
        assertThatThrownBy(() -> authenticationUseCase.login(loginRequest))
            .isInstanceOf(AuthenticationException.class)
            .hasMessageContaining(ErrorCode.EMAIL_VERIFICATION_REQUIRED.getMessage());
    }

    @Test
    @DisplayName("일반 회원가입 성공")
    void signupSuccess() {
        // given
        Account account = signupRequestDto.toEntity("encodedPassword");

        when(accountRepository.existsByEmail(signupRequestDto.getEmail())).thenReturn(false);
        when(passwordEncoder.encode(signupRequestDto.getPassword())).thenReturn("encodedPassword");
        when(accountRepository.save(any(Account.class))).thenReturn(account);

        // when
        AccountSignupResponseDto responseDto = authenticationUseCase.signup(signupRequestDto);

        // then
        assertThat(responseDto.getEmail()).isEqualTo(signupRequestDto.getEmail());
        verify(accountRepository).save(argThat(savedAccount ->
            savedAccount.getEmail().equals(signupRequestDto.getEmail()) &&
                savedAccount.getLoginTypes().contains(LoginType.LOCAL) &&
                savedAccount.getRole() == Role.ACCOUNT &&
                !savedAccount.isEmailVerified() &&
                savedAccount.getSubscriptionType() == SubscriptionType.FREE
        ));
    }

    @Test
    @DisplayName("중복 이메일로 인한 회원가입 실패")
    void signupDuplicateEmailFailure() {
        // given
        when(accountRepository.existsByEmail(signupRequestDto.getEmail())).thenReturn(true);

        // when & then
        assertThatThrownBy(() -> authenticationUseCase.signup(signupRequestDto))
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
            .emailVerified(true)
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
                savedAccount.isEmailVerified() &&
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
            .emailVerified(true)
            .loginTypes(Set.of(LoginType.GOOGLE))
            .build();

        when(accountRepository.findByEmail("test@example.com"))
            .thenReturn(Optional.of(existingAccount));

        // when
        AccountVO accountVO = authenticationUseCase.authenticateSocialUser("test@example.com", "google");

        // then
        assertThat(accountVO.getEmail()).isEqualTo("test@example.com");
        assertThat(existingAccount.getLoginTypes()).contains(LoginType.GOOGLE);
        assertThat(existingAccount.isEmailVerified()).isEqualTo(true);
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