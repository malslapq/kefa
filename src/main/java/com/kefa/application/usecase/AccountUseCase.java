package com.kefa.application.usecase;

import com.kefa.api.dto.account.request.AccountDeleteRequest;
import com.kefa.api.dto.account.request.AccountUpdateRequest;
import com.kefa.api.dto.account.request.AccountUpdatePasswordRequest;
import com.kefa.api.dto.account.response.AccountDeleteResponse;
import com.kefa.api.dto.account.response.AccountResponse;
import com.kefa.api.dto.account.response.AccountUpdateResponse;
import com.kefa.api.dto.account.response.AccountUpdatePasswordResponse;
import com.kefa.common.exception.AuthenticationException;
import com.kefa.common.exception.ErrorCode;
import com.kefa.domain.entity.Account;
import com.kefa.infrastructure.repository.AccountRepository;
import com.kefa.infrastructure.security.auth.AuthenticationInfo;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class AccountUseCase {

    private final AccountRepository accountRepository;
    private final PasswordEncoder passwordEncoder;

    @Transactional
    public AccountDeleteResponse delete(AccountDeleteRequest accountDeleteRequest, AuthenticationInfo authenticationInfo) {

        Account account = getAccount(authenticationInfo.getId());

        validatePassword(account.getPassword(), accountDeleteRequest.getPassword());

        accountRepository.delete(account);

        return AccountDeleteResponse.builder()
            .email(account.getEmail())
            .build();
    }

    @Transactional
    public AccountUpdatePasswordResponse updatePassword(AccountUpdatePasswordRequest accountUpdatePasswordRequest, AuthenticationInfo authenticationInfo) {

        Account account = getAccount(authenticationInfo.getId());

        validatePassword(account.getPassword(), accountUpdatePasswordRequest.getPrevPassword());

        if (accountUpdatePasswordRequest.getPrevPassword().equals(accountUpdatePasswordRequest.getNewPassword())) {
            throw new AuthenticationException(ErrorCode.NEW_PASSWORD_MUST_BE_DIFFERENT);
        }

        account.updatePassword(passwordEncoder.encode(accountUpdatePasswordRequest.getNewPassword()));

        return new AccountUpdatePasswordResponse();
    }

    @Transactional
    public AccountUpdateResponse updateAccount(AccountUpdateRequest accountUpdateRequest, AuthenticationInfo authenticationInfo) {

        Account account = getAccount(authenticationInfo.getId());
        account.updateName(accountUpdateRequest.getName());

        return AccountUpdateResponse.from(account);

    }

    @Transactional(readOnly = true)
    public AccountResponse findByAccountId(AuthenticationInfo authenticationInfo) {
        return AccountResponse.from(getAccount(authenticationInfo.getId()));
    }

    private void validatePassword(String encodedPassword, String inputPassword) {
        if (!passwordEncoder.matches(inputPassword, encodedPassword)) {
            throw new AuthenticationException(ErrorCode.INVALID_CREDENTIALS);
        }
    }

    private Account getAccount(Long targetId) {
        return accountRepository.findById(targetId).orElseThrow(() -> new AuthenticationException(ErrorCode.ACCOUNT_NOT_FOUND));
    }

}
