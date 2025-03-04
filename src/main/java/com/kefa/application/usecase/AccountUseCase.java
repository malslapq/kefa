package com.kefa.application.usecase;

import com.kefa.api.dto.request.AccountUpdateRequestDto;
import com.kefa.api.dto.request.UpdatePasswordRequestDto;
import com.kefa.api.dto.response.AccountResponseDto;
import com.kefa.api.dto.response.AccountUpdateResponseDto;
import com.kefa.api.dto.response.UpdatePasswordResponse;
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
    public UpdatePasswordResponse updatePassword(UpdatePasswordRequestDto updatePasswordRequestDto, AuthenticationInfo authenticationInfo) {

        Account account = getAccount(authenticationInfo.getId());

        if(!passwordEncoder.matches(updatePasswordRequestDto.getPrevPassword(), account.getPassword())){
            throw new AuthenticationException(ErrorCode.INVALID_CREDENTIALS);
        }

        if (updatePasswordRequestDto.getPrevPassword().equals(updatePasswordRequestDto.getNewPassword())) {
            throw new AuthenticationException(ErrorCode.NEW_PASSWORD_MUST_BE_DIFFERENT);
        }

        account.updatePassword(passwordEncoder.encode(updatePasswordRequestDto.getNewPassword()));

        return new UpdatePasswordResponse();
    }

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
