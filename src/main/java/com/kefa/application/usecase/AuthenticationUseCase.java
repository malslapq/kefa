package com.kefa.application.usecase;

import com.kefa.api.dto.account.request.AccountLoginRequest;
import com.kefa.api.dto.account.request.AccountSignupRequest;
import com.kefa.api.dto.account.request.AccountUpdatePasswordRequest;
import com.kefa.api.dto.account.response.AccountSignupResponse;
import com.kefa.api.dto.account.response.AccountUpdatePasswordResponse;
import com.kefa.api.dto.auth.request.PasswordResetDto;
import com.kefa.api.dto.auth.response.TokenResponse;
import com.kefa.common.exception.AccountException;
import com.kefa.common.exception.AuthenticationException;
import com.kefa.common.exception.ErrorCode;
import com.kefa.common.type.LoginType;
import com.kefa.common.type.Role;
import com.kefa.common.type.SubscriptionType;
import com.kefa.domain.entity.Account;
import com.kefa.domain.entity.RefreshToken;
import com.kefa.domain.entity.SocialInfo;
import com.kefa.domain.vo.AccountVO;
import com.kefa.infrastructure.repository.*;
import com.kefa.infrastructure.security.jwt.JwtProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.core.user.DefaultOAuth2User;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional
public class AuthenticationUseCase {

    private static final String ROLE_PREFIX = "ROLE_";

    private final AccountRepository accountRepository;
    private final RefreshTokenRepository refreshTokenRepository;
    private final SocialInfoRepository socialInfoRepository;
    private final ActiveTokenRepository activeTokenRepository;
    private final BlackListRepository blackListRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtProvider jwtProvider;
    private final PasswordResetCacheRepository passwordResetCacheRepository;

    public void validatePasswordReset(String passwordResetRequestEmail) {

        if (!accountRepository.existsByEmail(passwordResetRequestEmail)) {
            throw new AuthenticationException(ErrorCode.INVALID_CREDENTIALS);
        }

    }

    @Transactional(readOnly = true)
    public TokenResponse refreshToken(String refreshToken, String deviceId) {

        Long id = jwtProvider.getId(refreshToken);
        Account account = getAccountWithRefreshTokenFromAccountId(id);
        RefreshToken savedRefreshToken = account.getRefreshToken();

        validateRefreshToken(savedRefreshToken, refreshToken);
        validateDeviceId(savedRefreshToken.getDeviceId(), deviceId);

        String accessToken = jwtProvider.createAccessToken(id, account.getRole(), account.getName());

        Set<String> activeTokens = activeTokenRepository.findByAccountId(id);
        activeTokenRepository.invalidateAccountAllActiveTokens(id);
        blackListRepository.saveAll(activeTokens);

        activeTokenRepository.save(id, jwtProvider.getJwtId(accessToken));

        return TokenResponse.builder()
            .accessToken(accessToken)
            .build();
    }

    public AccountVO loginOrSignUp(String providerUserId, String email, LoginType loginType) {

        return socialInfoRepository.findByProviderUserIdWithAccount(providerUserId)
            .map(socialInfo -> AccountVO.from(socialInfo.getAccount()))
            .orElseGet(() -> authenticateSocialUser(providerUserId, email, loginType));
    }

    public DefaultOAuth2User createDefaultOauth2User(AccountVO accountVO, LoginType loginType, Map<String, Object> attributes) {

        String nameAttributeKey = loginType.getNameAttributeKey();

        attributes.put("accountId", accountVO.getId());
        attributes.put("email", accountVO.getEmail());

        return new DefaultOAuth2User(
            Collections.singleton(new SimpleGrantedAuthority(ROLE_PREFIX + accountVO.getRole())),
            attributes,
            nameAttributeKey
        );
    }

    @Transactional
    public void passwordReset(PasswordResetDto request) {

        String email = passwordResetCacheRepository.findByToken(request.getToken())
            .orElseThrow(() -> new AuthenticationException(ErrorCode.INVALID_PASSWORD_RESET_TOKEN));

        if (!request.getNewPassword().equals(request.getConfirmPassword())) {
            throw new AuthenticationException(ErrorCode.INVALID_PASSWORD);
        }

        Account account = accountRepository.findByEmail(email).orElseThrow(() -> new AuthenticationException(ErrorCode.NOT_FOUND_ACCOUNT));
        String encryptedPassword = passwordEncoder.encode(request.getNewPassword());
        account.updatePassword(encryptedPassword);

        accountRepository.save(account);
        passwordResetCacheRepository.delete(request.getToken());
    }

    @Transactional
    public AccountUpdatePasswordResponse updatePassword(AccountUpdatePasswordRequest request, Long loginAccountId) {

        Account account = accountRepository.findById(loginAccountId).orElseThrow(() -> new AccountException(ErrorCode.NOT_FOUND_ACCOUNT));

        validatePassword(request.getPrevPassword(), account.getPassword());

        if (request.getPrevPassword().equals(request.getNewPassword())) {
            throw new AuthenticationException(ErrorCode.NEW_PASSWORD_MUST_BE_DIFFERENT);
        }

        account.updatePassword(passwordEncoder.encode(request.getNewPassword()));

        return new AccountUpdatePasswordResponse();
    }

    @Transactional
    public TokenResponse login(AccountLoginRequest accountLoginRequest) {

        Account account = getAccountWithRefreshTokenFromEmail(accountLoginRequest.getEmail());

        validatePassword(accountLoginRequest.getPassword(), account.getPassword());
        validateEmailVerified(account);

        TokenResponse tokenResponse = issueJWT(account);
        RefreshToken refreshToken = account.getRefreshToken();

        if (refreshToken != null) {
            refreshToken.updateToken(tokenResponse.getRefreshToken(), jwtProvider.getTokenExpiration(tokenResponse.getRefreshToken()));
            refreshToken.updateDeviceId(accountLoginRequest.getDeviceId());
        } else {
            refreshToken = createRefreshTokenEntity(account, tokenResponse.getRefreshToken(), accountLoginRequest.getDeviceId());
            account.addRefreshToken(refreshToken);
        }

        String jwtId = jwtProvider.getJwtId(tokenResponse.getAccessToken());
        activeTokenRepository.save(account.getId(), jwtId);

        refreshTokenRepository.save(refreshToken);

        return tokenResponse;
    }

    @Transactional
    public void logout(Long loginAccountId, String jwtId) {

        Account account = getAccountWithRefreshTokenFromAccountId(loginAccountId);

        activeTokenRepository.delete(account.getId(), jwtId);
        blackListRepository.save(jwtId);
        RefreshToken refreshToken = account.getRefreshToken();

        if (refreshToken != null) {
            refreshToken.revoke();
        }

    }

    public AccountSignupResponse signup(AccountSignupRequest request) {

        validateDuplicateEmail(request.getEmail());
        Account account = createAccount(request);

        return AccountSignupResponse.from(account);

    }

    private Account getAccountWithRefreshTokenFromAccountId(Long accountId) {
        return accountRepository.findByIdWithRefreshToken(accountId).orElseThrow(() -> new AuthenticationException(ErrorCode.NOT_FOUND_ACCOUNT));
    }

    private void validateDeviceId(String deviceId, String requestDeviceId) {
        if (!deviceId.equals(requestDeviceId)) {
            throw new AuthenticationException(ErrorCode.INVALID_DEVICE_ID);
        }
    }

    private void validateRefreshToken(RefreshToken savedRefreshToken, String requestRefreshToken) {
        if (savedRefreshToken == null) {
            throw new AuthenticationException(ErrorCode.NOT_FOUND_REFRESH_TOKEN);
        }

        if (!savedRefreshToken.getToken().equals(requestRefreshToken)) {
            throw new AuthenticationException(ErrorCode.INVALID_REFRESH_TOKEN);
        }

        if (savedRefreshToken.isExpired()) {
            throw new AuthenticationException(ErrorCode.EXPIRED_JWT_TOKEN);
        }
    }

    private void validateEmailVerified(Account account) {
        if (!account.isEmailVerified())
            throw new AuthenticationException(ErrorCode.EMAIL_VERIFICATION_REQUIRED);
    }

    private RefreshToken createRefreshTokenEntity(Account account, String refreshToken, String deviceId) {

        return RefreshToken.builder()
            .token(refreshToken)
            .account(account)
            .deviceId(deviceId)
            .expiresAt(jwtProvider.getTokenExpiration(refreshToken))
            .build();
    }

    private TokenResponse issueJWT(Account account) {
        return TokenResponse.builder()
            .accessToken(jwtProvider.createAccessToken(account.getId(), account.getRole(), account.getName()))
            .refreshToken(jwtProvider.createRefreshToken(account.getId(), account.getRole(), account.getName()))
            .build();
    }

    private Account getAccountWithRefreshTokenFromEmail(String email) {
        return accountRepository.findByEmailWithRefreshToken(email).orElseThrow(() -> new AuthenticationException(ErrorCode.INVALID_CREDENTIALS));
    }

    private void validatePassword(String inputPassword, String savedPassword) {
        if (!passwordEncoder.matches(inputPassword, savedPassword)) {
            throw new AuthenticationException(ErrorCode.INVALID_CREDENTIALS);
        }
    }

    public AccountVO authenticateSocialUser(String providerUserId, String email, LoginType loginType) {

        return AccountVO.from(
            accountRepository.findByEmail(email)
                .orElseGet(() -> createSocialAccount(providerUserId, email, loginType))
        );
    }

    private Account createAccount(AccountSignupRequest request) {

        return accountRepository.save(
            request.toEntity(passwordEncoder.encode(request.getPassword())
            )
        );

    }

    private Account createSocialAccount(String providerUserId, String email, LoginType loginType) {

        Account account = Account.builder()
            .email(email)
            .name(email.split("@")[0])
            .password(passwordEncoder.encode(UUID.randomUUID().toString()))
            .subscriptionType(SubscriptionType.FREE)
            .emailVerified(true)
            .role(Role.FREE_ACCOUNT)
            .build();

        account = accountRepository.save(account);

        SocialInfo socialInfo = SocialInfo.builder()
            .providerUserId(providerUserId)
            .loginType(loginType)
            .build();

        account.addSocialInfo(socialInfo);

        socialInfoRepository.save(socialInfo);

        return account;
    }

    private void validateDuplicateEmail(String email) {

        if (accountRepository.existsByEmail(email)) {
            throw new AuthenticationException(ErrorCode.DUPLICATE_EMAIL);
        }

    }
}