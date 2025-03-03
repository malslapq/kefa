package com.kefa.application.usecase;

import com.kefa.api.dto.AccountUpdateRequestDto;
import com.kefa.api.dto.response.AccountResponseDto;
import com.kefa.api.dto.response.AccountUpdateResponseDto;
import com.kefa.common.exception.AuthenticationException;
import com.kefa.common.exception.ErrorCode;
import com.kefa.domain.entity.Account;
import com.kefa.infrastructure.repository.AccountRepository;
import com.kefa.infrastructure.security.auth.AuthenticationInfo;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class AccountUseCase {

    private final AccountRepository accountRepository;

    @Transactional
    public AccountUpdateResponseDto updateAccount(Long targetId, AccountUpdateRequestDto accountUpdateRequestDto, AuthenticationInfo authenticationInfo) {
        validateAccountAccess(targetId, authenticationInfo.getId());
        Account account = getAccount(targetId);
        account.updateName(accountUpdateRequestDto.getName());
        return AccountUpdateResponseDto.from(account);
    }

    @Transactional(readOnly = true)
    public AccountResponseDto getAccount(Long targetId, AuthenticationInfo authenticationInfo) {
        validateAccountAccess(targetId, authenticationInfo.getId());
        return AccountResponseDto.from(getAccount(targetId));
    }

    private Account getAccount(Long targetId) {
        return accountRepository.findById(targetId).orElseThrow(() -> new AuthenticationException(ErrorCode.ACCOUNT_NOT_FOUND));
    }

    private void validateAccountAccess(Long targetId, Long loginUserId) {
        if (!targetId.equals(loginUserId)) {
            throw new AuthenticationException(ErrorCode.ACCESS_DENIED);
        }
    }
}
