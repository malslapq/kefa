package com.kefa.application.usecase;

import com.kefa.api.dto.request.AccountLoginRequestDto;
import com.kefa.api.dto.request.AccountSignupRequestDto;
import com.kefa.api.dto.response.AccountSignupResponseDto;
import com.kefa.api.dto.response.TokenResponse;
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
import com.kefa.infrastructure.security.jwt.JwtProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

@Service
@RequiredArgsConstructor
@Transactional
public class AuthenticationUseCase {

    private final AccountRepository accountRepository;
    private final RefreshTokenRepository refreshTokenRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtProvider jwtProvider;

    public TokenResponse login(AccountLoginRequestDto accountLoginRequestDto) {

        Account account = getAccount(accountLoginRequestDto);
        validatePassword(accountLoginRequestDto.getPassword(), account.getPassword());
        validateEmailVerified(account);
        TokenResponse tokenResponse = issueJwt(account);

        RefreshToken refreshTokenEntity = createRefreshTokenEntity(account, tokenResponse, accountLoginRequestDto.getDeviceId());

        refreshTokenRepository.save(refreshTokenEntity);

        return tokenResponse;
    }

    private void validateEmailVerified(Account account){
        if(!account.isEmailVerified())
            throw new AuthenticationException(ErrorCode.EMAIL_VERIFICATION_REQUIRED);
    }

    private RefreshToken createRefreshTokenEntity(Account account, TokenResponse tokenResponse, String deviceId) {

        return RefreshToken.builder()
            .token(tokenResponse.getRefreshToken())
            .accountId(account.getId())
            .deviceId(deviceId)
            .expiresAt(jwtProvider.getTokenExpiration(tokenResponse.getRefreshToken()))
            .build();
    }

    private TokenResponse issueJwt(Account account){
        return TokenResponse.builder()
            .accessToken(jwtProvider.createAccessToken(account.getId(), account.getRole()))
            .refreshToken(jwtProvider.createAccessToken(account.getId(), account.getRole()))
            .build();
    }

    private Account getAccount(AccountLoginRequestDto accountLoginRequestDto) {
        return accountRepository.findByEmail(accountLoginRequestDto.getEmail()).orElseThrow(() -> new AuthenticationException(ErrorCode.INVALID_CREDENTIALS));
    }

    private void validatePassword(String inputPassword, String savedPassword) {
        if(!passwordEncoder.matches(inputPassword, savedPassword)){
            throw new AuthenticationException(ErrorCode.INVALID_CREDENTIALS);
        }
    }

    public AccountSignupResponseDto signup(AccountSignupRequestDto request) {

        validateDuplicateEmail(request.getEmail());
        Account account = createAccount(request);
        return AccountSignupResponseDto.from(account);

    }

    public AccountVO authenticateSocialUser(String email, String provider) {

        LoginType loginType = validateAndGetLoginType(provider);

        return AccountVO.from(accountRepository.findByEmail(email).orElseGet(
            () -> createSocialAccount(email, loginType))
        );

    }

    private Account createAccount(AccountSignupRequestDto request) {

        return accountRepository.save(
            request.toEntity(passwordEncoder.encode(request.getPassword())
            )
        );

    }

    private Account createSocialAccount(String email, LoginType loginType) {

        return accountRepository.save(Account.builder()
            .email(email)
            .name(email.split("@")[0])
            .password(passwordEncoder.encode(UUID.randomUUID().toString()))
            .subscriptionType(SubscriptionType.FREE)
            .emailVerified(true)
            .role(Role.ACCOUNT)
            .loginTypes(new HashSet<>(Set.of(loginType)))
            .build()
        );

    }

    private LoginType validateAndGetLoginType(String provider) {

        try {
            return LoginType.valueOf(provider.toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new AuthenticationException(ErrorCode.UNSUPPORTED_SOCIAL_PROVIDER);
        }

    }

    private void validateDuplicateEmail(String email) {

        if (accountRepository.existsByEmail(email)) {
            throw new AuthenticationException(ErrorCode.DUPLICATE_EMAIL);
        }

    }
}