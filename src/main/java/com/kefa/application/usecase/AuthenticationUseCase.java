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
import com.kefa.domain.entity.RefreshToken;
import com.kefa.domain.type.LoginType;
import com.kefa.domain.type.Role;
import com.kefa.domain.type.SubscriptionType;
import com.kefa.domain.vo.AccountVO;
import com.kefa.infrastructure.repository.AccountRepository;
import com.kefa.infrastructure.repository.RefreshTokenRepository;
import com.kefa.infrastructure.repository.SocialInfoRepository;
import com.kefa.infrastructure.security.jwt.JwtProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.core.user.DefaultOAuth2User;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.Map;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional
public class AuthenticationUseCase {

    private static final String ROLE_PREFIX = "ROLE_";

    private final AccountRepository accountRepository;
    private final RefreshTokenRepository refreshTokenRepository;
    private final SocialInfoRepository socialInfoRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtProvider jwtProvider;

    public TokenResponse refreshToken(String refreshToken, String deviceId) {

        Long id = jwtProvider.getId(refreshToken);
        Account account = accountRepository.findByIdWithRefreshToken(id).orElseThrow(() -> new AuthenticationException(ErrorCode.NOT_FOUND_ACCOUNT));
        RefreshToken savedRefreshToken = account.getRefreshToken();

        validateRefreshToken(savedRefreshToken, refreshToken);
        validateDeviceId(savedRefreshToken.getDeviceId(), deviceId);

        TokenResponse tokenResponse = issueJWT(account);

        savedRefreshToken.updateToken(tokenResponse.getRefreshToken());

        refreshTokenRepository.save(savedRefreshToken);

        return tokenResponse;
    }

    public AccountVO loginOrSignUp(String providerUserId, String email) {

        return socialInfoRepository.findByProviderUserIdWithAccount(providerUserId)
            .map(socialInfo -> AccountVO.from(socialInfo.getAccount()))
            .orElseGet(() -> authenticateSocialUser(email));
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

        Account account = getAccountFromEmail(accountLoginRequest.getEmail());

        validatePassword(accountLoginRequest.getPassword(), account.getPassword());
        validateEmailVerified(account);

        TokenResponse tokenResponse = issueJWT(account);

        if (account.getRefreshToken() != null) {
            account.getRefreshToken().updateToken(tokenResponse.getRefreshToken());
        } else {
            account.addRefreshToken(createRefreshTokenEntity(account, tokenResponse.getRefreshToken(), accountLoginRequest.getDeviceId()));
        }

        refreshTokenRepository.save(account.getRefreshToken());

        return tokenResponse;
    }

    public AccountSignupResponse signup(AccountSignupRequest request) {

        validateDuplicateEmail(request.getEmail());
        Account account = createAccount(request);

        return AccountSignupResponse.from(account);

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

    private Account getAccountFromEmail(String email) {
        return accountRepository.findByEmailWithRefreshToken(email).orElseThrow(() -> new AuthenticationException(ErrorCode.INVALID_CREDENTIALS));
    }

    private void validatePassword(String inputPassword, String savedPassword) {
        if (!passwordEncoder.matches(inputPassword, savedPassword)) {
            throw new AuthenticationException(ErrorCode.INVALID_CREDENTIALS);
        }
    }

    public AccountVO authenticateSocialUser(String email) {

        return AccountVO.from(accountRepository.findByEmail(email).orElseGet(
            () -> createSocialAccount(email))
        );
    }

    private Account createAccount(AccountSignupRequest request) {

        return accountRepository.save(
            request.toEntity(passwordEncoder.encode(request.getPassword())
            )
        );

    }

    private Account createSocialAccount(String email) {

        return accountRepository.save(Account.builder()
            .email(email)
            .name(email.split("@")[0])
            .password(passwordEncoder.encode(UUID.randomUUID().toString()))
            .subscriptionType(SubscriptionType.FREE)
            .emailVerified(true)
            .role(Role.FREE_ACCOUNT)
            .build()
        );
    }

    private void validateDuplicateEmail(String email) {

        if (accountRepository.existsByEmail(email)) {
            throw new AuthenticationException(ErrorCode.DUPLICATE_EMAIL);
        }

    }
}