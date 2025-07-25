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

    /**
     * Validates that an account exists for the given email before allowing a password reset.
     *
     * @param passwordResetRequestEmail the email address to validate for password reset eligibility
     * @throws AuthenticationException if no account is found with the provided email
     */
    public void validatePasswordReset(String passwordResetRequestEmail) {

        if (!accountRepository.existsByEmail(passwordResetRequestEmail)) {
            throw new AuthenticationException(ErrorCode.INVALID_CREDENTIALS);
        }

    }

    /**
     * Issues a new access token using a valid refresh token and device ID, invalidating all previous active tokens for the account.
     *
     * Validates the provided refresh token and device ID, generates a new access token, blacklists all previously active tokens for the account, and stores the new access token as active.
     *
     * @param refreshToken the refresh token used to authenticate the request
     * @param deviceId the device identifier associated with the refresh token
     * @return a {@link TokenResponse} containing the new access token
     * @throws AuthenticationException if the refresh token or device ID is invalid
     */
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

    /**
     * Returns the account associated with the given social provider user ID, or creates a new social account if none exists.
     *
     * If a social account matching the provider user ID is found, returns its account as an {@code AccountVO}. Otherwise, authenticates the social user by creating or retrieving an account using the provided email and login type.
     *
     * @param providerUserId the unique user ID from the social login provider
     * @param email the email address associated with the social account
     * @param loginType the type of social login provider
     * @return the account information as an {@code AccountVO}
     */
    public AccountVO loginOrSignUp(String providerUserId, String email, LoginType loginType) {

        return socialInfoRepository.findByProviderUserIdWithAccount(providerUserId)
            .map(socialInfo -> AccountVO.from(socialInfo.getAccount()))
            .orElseGet(() -> authenticateSocialUser(providerUserId, email, loginType));
    }

    /**
     * Creates a DefaultOAuth2User with the given account information, login type, and attributes.
     *
     * Adds the account ID and email to the OAuth2 attributes and assigns a granted authority based on the account's role.
     *
     * @param accountVO the account value object containing user details
     * @param loginType the type of social login provider
     * @param attributes the OAuth2 attributes to be enriched and used for the user
     * @return a DefaultOAuth2User representing the authenticated user with appropriate authorities and attributes
     */
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

    /**
     * Resets the account password using a valid password reset token and new password data.
     *
     * Validates the reset token, ensures the new password and confirmation match, updates the account's password,
     * and removes the used reset token from the cache.
     *
     * @param request the password reset request containing the reset token, new password, and confirmation
     * @throws AuthenticationException if the token is invalid, passwords do not match, or the account is not found
     */
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

    /**
     * Updates the password for the specified account after validating the previous password and ensuring the new password is different.
     *
     * @param request the password update request containing previous and new passwords
     * @param loginAccountId the ID of the account to update
     * @return an empty response indicating successful password update
     * @throws AccountException if the account is not found
     * @throws AuthenticationException if the previous password is incorrect or the new password matches the previous password
     */
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

    /**
     * Authenticates a user with email and password, issues new JWT access and refresh tokens, and updates token records.
     *
     * Validates the provided credentials and email verification status, updates or creates the refresh token entity with the device ID, saves the active access token, and returns the issued tokens.
     *
     * @param accountLoginRequest the login request containing email, password, and device ID
     * @return a TokenResponse containing the new access and refresh tokens
     */
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

    /**
     * Logs out the account by invalidating the access token and revoking the refresh token.
     *
     * Removes the specified JWT ID from the active tokens, adds it to the blacklist, and revokes the refresh token if present for the given account.
     *
     * @param loginAccountId the ID of the account to log out
     * @param jwtId the JWT ID of the access token to invalidate
     */
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

    /**
     * Generates and returns a new access token and refresh token for the specified account.
     *
     * @param account the account for which to issue JWT tokens
     * @return a TokenResponse containing the generated access and refresh tokens
     */
    private TokenResponse issueJWT(Account account) {
        return TokenResponse.builder()
            .accessToken(jwtProvider.createAccessToken(account.getId(), account.getRole(), account.getName()))
            .refreshToken(jwtProvider.createRefreshToken(account.getId(), account.getRole(), account.getName()))
            .build();
    }

    /**
     * Retrieves an account by email, including its associated refresh token, or throws an exception if not found.
     *
     * @param email the email address of the account to retrieve
     * @return the account with its refresh token
     * @throws AuthenticationException if no account with the given email exists
     */
    private Account getAccountWithRefreshTokenFromEmail(String email) {
        return accountRepository.findByEmailWithRefreshToken(email).orElseThrow(() -> new AuthenticationException(ErrorCode.INVALID_CREDENTIALS));
    }

    /**
     * Validates that the provided password matches the stored encoded password.
     *
     * @param inputPassword the raw password input to validate
     * @param savedPassword the encoded password stored for the account
     * @throws AuthenticationException if the passwords do not match
     */
    private void validatePassword(String inputPassword, String savedPassword) {
        if (!passwordEncoder.matches(inputPassword, savedPassword)) {
            throw new AuthenticationException(ErrorCode.INVALID_CREDENTIALS);
        }
    }

    /**
     * Retrieves an account by email or creates a new social account if none exists, then returns its value object.
     *
     * @param providerUserId the unique user ID from the social login provider
     * @param email the email address associated with the social account
     * @param loginType the type of social login provider
     * @return the account value object for the authenticated or newly created social user
     */
    public AccountVO authenticateSocialUser(String providerUserId, String email, LoginType loginType) {

        return AccountVO.from(
            accountRepository.findByEmail(email)
                .orElseGet(() -> createSocialAccount(providerUserId, email, loginType))
        );
    }

    /**
     * Creates and saves a new account using the provided signup request, encoding the password before persistence.
     *
     * @param request the signup request containing account details and raw password
     * @return the newly created and saved Account entity
     */
    private Account createAccount(AccountSignupRequest request) {

        return accountRepository.save(
            request.toEntity(passwordEncoder.encode(request.getPassword())
            )
        );

    }

    /**
     * Creates a new account linked to a social login provider and saves it with default values.
     *
     * @param providerUserId the unique identifier from the social login provider
     * @param email the user's email address
     * @param loginType the type of social login provider
     * @return the newly created Account entity associated with the social login
     */
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

    /**
     * Checks if the provided email is already registered and throws an exception if it is.
     *
     * @param email the email address to check for duplication
     * @throws AuthenticationException if the email is already associated with an existing account
     */
    private void validateDuplicateEmail(String email) {

        if (accountRepository.existsByEmail(email)) {
            throw new AuthenticationException(ErrorCode.DUPLICATE_EMAIL);
        }

    }
}