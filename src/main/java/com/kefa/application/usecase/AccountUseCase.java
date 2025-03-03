package com.kefa.application.usecase;

import com.kefa.api.dto.response.AccountResponseDto;
import com.kefa.common.exception.AuthenticationException;
import com.kefa.common.exception.ErrorCode;
import com.kefa.infrastructure.repository.AccountRepository;
import com.kefa.infrastructure.security.auth.AuthenticationInfo;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AccountUseCase {

    private final AccountRepository accountRepository;

    public AccountResponseDto getAccount(Long targetId, AuthenticationInfo authenticationInfo) {
        validateAccountAccess(targetId, authenticationInfo.getId());
        return AccountResponseDto.from(accountRepository.findById(targetId).orElseThrow(() -> new AuthenticationException(ErrorCode.ACCOUNT_NOT_FOUND)));
    }

    private void validateAccountAccess(Long targetId, Long loginUserId) {
        if(!targetId.equals(loginUserId)){
            throw new AuthenticationException(ErrorCode.ACCESS_DENIED);
        }
    }

}
