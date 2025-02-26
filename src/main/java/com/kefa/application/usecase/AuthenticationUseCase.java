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
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional
public class AuthenticationUseCase {

    private final AccountRepository accountRepository;
    private final PasswordEncoder passwordEncoder;

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