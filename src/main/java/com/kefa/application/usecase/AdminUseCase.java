package com.kefa.application.usecase;

import com.kefa.api.dto.account.command.GetAccountsCommand;
import com.kefa.api.dto.account.response.AccountDetailResponse;
import com.kefa.api.dto.consulting.response.PagedResponse;
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
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AdminUseCase {

    private final AccountRepository accountRepository;

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

}
