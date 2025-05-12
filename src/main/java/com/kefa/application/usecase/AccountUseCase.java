package com.kefa.application.usecase;

import com.kefa.api.dto.account.request.AccountDeleteRequest;
import com.kefa.api.dto.account.request.AccountUpdateRequest;
import com.kefa.api.dto.account.response.AccountDeleteResponse;
import com.kefa.api.dto.account.response.AccountResponse;
import com.kefa.api.dto.account.response.AccountUpdateResponse;
import com.kefa.api.dto.account.response.SocialInfoDeleteResponse;
import com.kefa.common.exception.AccountException;
import com.kefa.common.exception.ErrorCode;
import com.kefa.domain.entity.Account;
import com.kefa.domain.entity.SocialInfo;
import com.kefa.infrastructure.repository.AccountRepository;
import com.kefa.infrastructure.repository.SocialInfoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class AccountUseCase {

    private final AccountRepository accountRepository;
    private final PasswordEncoder passwordEncoder;
    private final SocialInfoRepository socialInfoRepository;

    @Transactional
    public SocialInfoDeleteResponse deleteSocialInfo(Long accountId, Long socialInfoId) {

        Account account = accountRepository.findByIdWithSocialInfos(accountId).orElseThrow(() -> new AccountException(ErrorCode.NOT_FOUND_ACCOUNT));

        SocialInfo socialInfo = account.getSocialInfos().stream()
            .filter(si -> si.getId().equals(socialInfoId))
            .findFirst()
            .orElseThrow(() -> new AccountException(ErrorCode.NOT_FOUND_SOCIALINFO));

        account.removeSocialInfo(socialInfo);
        socialInfoRepository.delete(socialInfo);

        return new SocialInfoDeleteResponse();
    }

    @Transactional
    public AccountDeleteResponse delete(AccountDeleteRequest accountDeleteRequest, Long loginAccountId) {

        Account account = getAccount(loginAccountId);

        validatePassword(account.getPassword(), accountDeleteRequest.getPassword());

        accountRepository.delete(account);

        return AccountDeleteResponse.builder()
            .email(account.getEmail())
            .build();
    }

    @Transactional
    public AccountUpdateResponse updateAccount(AccountUpdateRequest accountUpdateRequest, Long loginAccountId) {

        Account account = getAccount(loginAccountId);
        account.updateName(accountUpdateRequest.getName());

        return AccountUpdateResponse.from(account);

    }

    @Transactional(readOnly = true)
    public AccountResponse findByAccountId(Long loginAccountId) {
        return AccountResponse.from(getAccount(loginAccountId));
    }

    private void validatePassword(String encodedPassword, String inputPassword) {
        if (!passwordEncoder.matches(inputPassword, encodedPassword)) {
            throw new AccountException(ErrorCode.INVALID_CREDENTIALS);
        }
    }

    private Account getAccount(Long targetId) {
        return accountRepository.findById(targetId).orElseThrow(() -> new AccountException(ErrorCode.NOT_FOUND_ACCOUNT));
    }
}
