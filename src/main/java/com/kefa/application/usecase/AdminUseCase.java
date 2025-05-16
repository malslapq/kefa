package com.kefa.application.usecase;

import com.kefa.api.dto.AccountUpdateSubscriptionTypeRequest;
import com.kefa.api.dto.account.command.GetAccountsCommand;
import com.kefa.api.dto.account.request.AccountUpdatePasswordRequestFromAdmin;
import com.kefa.api.dto.account.request.AccountUpdateRequest;
import com.kefa.api.dto.account.request.AccountUpdateRoleRequest;
import com.kefa.api.dto.account.response.AccountDetailResponse;
import com.kefa.api.dto.consulting.response.PagedResponse;
import com.kefa.common.exception.AdminException;
import com.kefa.common.exception.ErrorCode;
import com.kefa.common.type.AccountsSearchType;
import com.kefa.common.type.Role;
import com.kefa.common.type.SubscriptionType;
import com.kefa.domain.entity.Account;
import com.kefa.infrastructure.repository.AccountRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AdminUseCase {

    private final AccountRepository accountRepository;
    private final PasswordEncoder passwordEncoder;

    @Transactional
    public void deleteAccount(Long accountId) {
        accountRepository.deleteById(accountId);
    }

    @Transactional
    public AccountDetailResponse updateAccountSubscriptionType(Long accountId, AccountUpdateSubscriptionTypeRequest request) {

        Account account = getAccountFromId(accountId);

        account.updateSubscriptionType(request.getSubscriptionType());

        return saveAndReturnAccountDetailResponse(account);
    }

    @Transactional
    public AccountDetailResponse updateAccountRole(Long accountId, AccountUpdateRoleRequest request) {

        Account account = getAccountFromId(accountId);

        account.updateRole(request.getRole());

        return saveAndReturnAccountDetailResponse(account);
    }

    @Transactional
    public AccountDetailResponse updateAccountPassword(Long accountId, AccountUpdatePasswordRequestFromAdmin request) {

        Account account = getAccountFromId(accountId);
        String encodedNewPassword = passwordEncoder.encode(request.getNewPassword());
        account.updatePassword(encodedNewPassword);

        return saveAndReturnAccountDetailResponse(account);
    }

    @Transactional
    public AccountDetailResponse updateAccount(Long accountId, AccountUpdateRequest request) {

        Account account = getAccountFromId(accountId);
        applyUpdatesFromRequest(account, request);

        return saveAndReturnAccountDetailResponse(account);
    }

    @Transactional(readOnly = true)
    public PagedResponse<AccountDetailResponse> getAccounts(GetAccountsCommand command) {

        Pageable pageable = PageRequest.of(
            command.getPage(),
            command.getSize(),
            Sort.by(Sort.Direction.DESC, "createdAt")
        );

        AccountsSearchType type = AccountsSearchType.from(command.getSearchType());

        Page<Account> accountPage = getAccountsFromSearchType(type, command.getKeyword(), pageable);

        List<AccountDetailResponse> accountDetailResponses = accountPage.getContent().stream()
            .map(AccountDetailResponse::from)
            .collect(Collectors.toList());

        return PagedResponse.<AccountDetailResponse>builder()
            .content(accountDetailResponses)
            .page(accountPage.getNumber())
            .size(accountPage.getSize())
            .totalElements(accountPage.getTotalElements())
            .totalPages(accountPage.getTotalPages())
            .build();
    }

    private Page<Account> getAccountsFromSearchType(AccountsSearchType type, String keyword, Pageable pageable) {

        if (!StringUtils.hasText(keyword)) {
            return accountRepository.findAll(pageable);
        }

        return switch (type) {
            case NAME -> accountRepository.findByNameContaining(keyword, pageable);
            case EMAIL -> accountRepository.findByEmailContaining(keyword, pageable);
            case ROLE -> accountRepository.findByRole(Role.from(keyword), pageable);
            case SUBSCRIPTION_TYPE ->
                accountRepository.findBySubscriptionType(SubscriptionType.from(keyword), pageable);
            case TOTAL -> accountRepository.findAll(pageable);
        };
    }

    private Account getAccountFromId(Long accountId) {
        return accountRepository.findById(accountId).orElseThrow(() -> new AdminException(ErrorCode.NOT_FOUND_ACCOUNT));
    }

    private void applyUpdatesFromRequest(Account account, AccountUpdateRequest request) {
        if (!account.getEmail().equals(request.getEmail())) {
            account.updateEmail(request.getEmail());
        }
        if (!account.getName().equals(request.getName())) {
            account.updateName(request.getName());
        }
        if (!account.getRole().equals(request.getRole())) {
            account.updateRole(request.getRole());
        }
        if (!account.getSubscriptionType().equals(request.getSubscriptionType())) {
            account.updateSubscriptionType(request.getSubscriptionType());
        }
        if (account.isEmailVerified() != request.isEmailVerified()) {
            account.updateEmailVerified(request.isEmailVerified());
        }
    }

    private AccountDetailResponse saveAndReturnAccountDetailResponse(Account account) {
        accountRepository.save(account);
        return AccountDetailResponse.from(account);
    }
}
