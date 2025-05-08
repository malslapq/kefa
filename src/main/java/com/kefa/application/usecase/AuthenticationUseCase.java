package com.kefa.application.usecase;

import com.kefa.api.dto.account.request.AccountLoginRequest;
import com.kefa.api.dto.account.request.AccountSignupRequest;
import com.kefa.api.dto.account.response.AccountSignupResponse;
import com.kefa.api.dto.account.response.TokenResponse;
import com.kefa.common.exception.AuthenticationException;
import com.kefa.common.exception.ErrorCode;
import com.kefa.domain.entity.Account;
import com.kefa.domain.entity.RefreshToken;
import com.kefa.domain.type.Role;
import com.kefa.domain.type.SubscriptionType;
import com.kefa.domain.vo.AccountVO;
import com.kefa.infrastructure.repository.AccountRepository;
import com.kefa.infrastructure.repository.RefreshTokenRepository;
import com.kefa.infrastructure.security.jwt.JwtProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional
public class AuthenticationUseCase {

    private final AccountRepository accountRepository;
    private final RefreshTokenRepository refreshTokenRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtProvider jwtProvider;

    public TokenResponse login(AccountLoginRequest accountLoginRequest) {

        Account account = getAccountFromEmail(accountLoginRequest.getEmail());

        validatePassword(accountLoginRequest.getPassword(), account.getPassword());
        validateEmailVerified(account);

        TokenResponse tokenResponse = issueJwt(account);

        RefreshToken refreshToken = refreshTokenRepository.findByAccountId(account.getId())
            .orElse(createRefreshTokenEntity(account, tokenResponse, accountLoginRequest.getDeviceId()));

        refreshTokenRepository.save(refreshToken);

        return tokenResponse;
    }

    private void validateEmailVerified(Account account) {
        if (!account.isEmailVerified())
            throw new AuthenticationException(ErrorCode.EMAIL_VERIFICATION_REQUIRED);
    }

    private RefreshToken createRefreshTokenEntity(Account account, TokenResponse tokenResponse, String deviceId) {

        return RefreshToken.builder()
            .token(tokenResponse.getRefreshToken())
            .account(account)
            .deviceId(deviceId)
            .expiresAt(jwtProvider.getTokenExpiration(tokenResponse.getRefreshToken()))
            .build();
    }

    private TokenResponse issueJwt(Account account) {
        return TokenResponse.builder()
            .accessToken(jwtProvider.createAccessToken(account.getId(), account.getRole(), account.getName()))
            .refreshToken(jwtProvider.createAccessToken(account.getId(), account.getRole(), account.getName()))
            .build();
    }

    private Account getAccountFromEmail(String email) {
        return accountRepository.findByEmail(email).orElseThrow(() -> new AuthenticationException(ErrorCode.INVALID_CREDENTIALS));
    }

    private void validatePassword(String inputPassword, String savedPassword) {
        if (!passwordEncoder.matches(inputPassword, savedPassword)) {
            throw new AuthenticationException(ErrorCode.INVALID_CREDENTIALS);
        }
    }

    public AccountSignupResponse signup(AccountSignupRequest request) {

        validateDuplicateEmail(request.getEmail());
        Account account = createAccount(request);

        return AccountSignupResponse.from(account);

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