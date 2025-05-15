package com.kefa.application.usecase;

import com.kefa.api.dto.account.request.AccountLoginRequest;
import com.kefa.api.dto.account.request.AccountSignupRequest;
import com.kefa.api.dto.account.request.AccountUpdatePasswordRequest;
import com.kefa.api.dto.account.response.AccountSignupResponse;
import com.kefa.api.dto.account.response.AccountUpdatePasswordResponse;
import com.kefa.api.dto.auth.response.TokenResponse;
import com.kefa.common.exception.AccountException;
import com.kefa.common.exception.AuthenticationException;
import com.kefa.common.exception.ErrorCode;
import com.kefa.domain.entity.Account;
import com.kefa.common.type.Role;
import com.kefa.common.type.SubscriptionType;
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

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
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

    @Mock
    private EmailVerificationUseCase emailVerificationUseCase;

    @InjectMocks
    private AuthenticationUseCase authenticationUseCase;

    private Account account;
    private final Long targetId = 1L;
    private AccountSignupRequest signupRequestDto;
    private AccountLoginRequest loginRequest;


    @BeforeEach
    void setUp() {
        account = Account.builder()
            .id(1L)
            .email("test@test.com")
            .name("test")
            .password("encodedPassword")
            .role(Role.FREE_ACCOUNT)
            .subscriptionType(SubscriptionType.FREE)
            .emailVerified(true)
            .build();

        signupRequestDto = AccountSignupRequest.builder()
            .email("test@example.com")
            .password("password123")
            .build();

        loginRequest = AccountLoginRequest.builder()
            .email("test@test.com")
            .password("encodedPassword")
            .deviceId("device1")
            .build();
    }

    @DisplayName("비밀번호 변경 성공")
    @Test
    void updatePasswordSuccess() {
        // given
        String prevPassword = "prevPass123!";
        String newPassword = "newPass456@";
        String encodedNewPassword = "encodedNewPassword";
        AccountUpdatePasswordRequest request = AccountUpdatePasswordRequest.builder()
            .prevPassword(prevPassword)
            .newPassword(newPassword)
            .build();

        given(accountRepository.findById(targetId)).willReturn(Optional.of(account));
        given(passwordEncoder.matches(prevPassword, account.getPassword())).willReturn(true);
        given(passwordEncoder.encode(newPassword)).willReturn(encodedNewPassword);

        // when
        AccountUpdatePasswordResponse response = authenticationUseCase.updatePassword(request, targetId);

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
        AccountUpdatePasswordRequest request = AccountUpdatePasswordRequest.builder()
            .prevPassword("wrongPass123!")
            .newPassword("newPass456@")
            .build();

        given(accountRepository.findById(targetId)).willReturn(Optional.of(account));
        given(passwordEncoder.matches(request.getPrevPassword(), account.getPassword())).willReturn(false);

        // when & then
        assertThatThrownBy(() -> authenticationUseCase.updatePassword(request, targetId))
            .isInstanceOf(AuthenticationException.class)
            .hasMessage(ErrorCode.INVALID_CREDENTIALS.getMessage());
    }

    @DisplayName("비밀번호 변경 실패 - 새 비밀번호가 현재와 동일")
    @Test
    void updatePasswordFailSamePassword() {
        // given
        String password = "samePass123!";
        AccountUpdatePasswordRequest request = AccountUpdatePasswordRequest.builder()
            .prevPassword(password)
            .newPassword(password)
            .build();

        given(accountRepository.findById(targetId)).willReturn(Optional.of(account));
        given(passwordEncoder.matches(password, account.getPassword())).willReturn(true);

        // when & then
        assertThatThrownBy(() -> authenticationUseCase.updatePassword(request, targetId))
            .isInstanceOf(AuthenticationException.class)
            .hasMessage(ErrorCode.NEW_PASSWORD_MUST_BE_DIFFERENT.getMessage());
    }

    @DisplayName("비밀번호 변경 실패 - 계정 없음")
    @Test
    void updatePasswordFailAccountNotFound() {
        // given
        AccountUpdatePasswordRequest request = AccountUpdatePasswordRequest.builder()
            .prevPassword("prevPass123!")
            .newPassword("newPass456@")
            .build();

        given(accountRepository.findById(targetId)).willReturn(Optional.empty());

        // when & then
        assertThatThrownBy(() ->
            authenticationUseCase.updatePassword(request, targetId)
        )
            .isInstanceOf(AccountException.class)
            .hasMessage(ErrorCode.NOT_FOUND_ACCOUNT.getMessage());
    }

    @Test
    @DisplayName("로그인 성공")
    void loginSuccess() {
        // given
        String accessToken = "accessToken";
        String refreshToken = "refreshToken";
        LocalDateTime expirationTime = LocalDateTime.now().plusDays(2);

        when(accountRepository.findByEmailWithRefreshToken(loginRequest.getEmail())).thenReturn(Optional.of(account));
        when(passwordEncoder.matches(account.getPassword(), loginRequest.getPassword())).thenReturn(true);
        when(jwtProvider.createAccessToken(account.getId(), account.getRole(), account.getName())).thenReturn(accessToken);
        when(jwtProvider.createRefreshToken(account.getId(), account.getRole(), account.getName())).thenReturn(refreshToken);
        when(jwtProvider.getTokenExpiration(refreshToken)).thenReturn(expirationTime);

        // when
        TokenResponse response = authenticationUseCase.login(loginRequest);

        // then
        assertThat(response.getAccessToken()).isEqualTo(accessToken);
        assertThat(response.getRefreshToken()).isEqualTo(refreshToken);
        verify(refreshTokenRepository).save(argThat(savedToken ->
            savedToken.getToken().equals(refreshToken) &&
                savedToken.getDeviceId().equals(loginRequest.getDeviceId()) &&
                savedToken.getAccount().getId().equals(account.getId()) &&
                savedToken.getExpiresAt().equals(expirationTime)
        ));
    }

    @Test
    @DisplayName("존재하지 않는 이메일로 로그인 실패")
    void loginFailWhenEmailNotFound() {

        when(accountRepository.findByEmailWithRefreshToken(loginRequest.getEmail())).thenReturn(Optional.empty());

        // when & then
        assertThatThrownBy(() -> authenticationUseCase.login(loginRequest))
            .isInstanceOf(AuthenticationException.class)
            .hasMessageContaining(ErrorCode.INVALID_CREDENTIALS.getMessage());
    }

    @Test
    @DisplayName("비밀번호 다름으로 인한 로그인 실패")
    void loginFailWhenPasswordWrong() {

        when(accountRepository.findByEmailWithRefreshToken(loginRequest.getEmail())).thenReturn(Optional.of(account));
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
        Account account = Account.builder()
            .id(1L)
            .email("test@example.com")
            .password("encodedPassword")
            .name("name")
            .subscriptionType(SubscriptionType.FREE)
            .role(Role.FREE_ACCOUNT)
            .emailVerified(false)  // 미인증 계정
            .build();

        when(accountRepository.findByEmailWithRefreshToken(loginRequest.getEmail())).thenReturn(Optional.of(account));
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
        AccountSignupResponse responseDto = authenticationUseCase.signup(signupRequestDto);

        // then
        assertThat(responseDto.getEmail()).isEqualTo(signupRequestDto.getEmail());
        verify(accountRepository).save(argThat(savedAccount ->
            savedAccount.getEmail().equals(signupRequestDto.getEmail()) &&
                savedAccount.getRole() == Role.FREE_ACCOUNT &&
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
            .role(Role.FREE_ACCOUNT)
            .emailVerified(true)
            .build();

        when(accountRepository.findByEmail("test@example.com")).thenReturn(Optional.empty());
        when(accountRepository.save(any(Account.class))).thenReturn(newAccount);

        // when
        AccountVO accountVO = authenticationUseCase.authenticateSocialUser("test@example.com");

        // then
        assertThat(accountVO.getEmail()).isEqualTo("test@example.com");
        verify(accountRepository).save(argThat(savedAccount ->
            savedAccount.getEmail().equals("test@example.com") &&
                savedAccount.getRole() == Role.FREE_ACCOUNT &&
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
            .role(Role.FREE_ACCOUNT)
            .emailVerified(true)
            .build();

        when(accountRepository.findByEmail("test@example.com"))
            .thenReturn(Optional.of(existingAccount));

        // when
        AccountVO accountVO = authenticationUseCase.authenticateSocialUser("test@example.com");

        // then
        assertThat(accountVO.getEmail()).isEqualTo("test@example.com");
        assertThat(existingAccount.isEmailVerified()).isEqualTo(true);
        verify(accountRepository, never()).save(any());
    }

}