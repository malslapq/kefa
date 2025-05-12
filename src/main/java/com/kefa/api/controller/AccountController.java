package com.kefa.api.controller;

import com.kefa.api.dto.account.request.AccountDeleteRequest;
import com.kefa.api.dto.account.request.AccountUpdateRequest;
import com.kefa.api.dto.account.response.AccountDeleteResponse;
import com.kefa.api.dto.account.response.AccountResponse;
import com.kefa.api.dto.account.response.AccountUpdateResponse;
import com.kefa.api.dto.account.response.SocialInfoDeleteResponse;
import com.kefa.application.service.AccountService;
import com.kefa.common.response.ApiResponse;
import com.kefa.infrastructure.security.auth.LoginAccount;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
public class AccountController {

    private final AccountService accountService;

    @DeleteMapping("/accounts/social-info/{socialInfoId}")
    public ApiResponse<SocialInfoDeleteResponse> deleteSocialInfo(@AuthenticationPrincipal LoginAccount loginAccount, @PathVariable Long socialInfoId) {
        return ApiResponse.success(accountService.deleteSocialInfo(loginAccount.getId(), socialInfoId));
    }

    @DeleteMapping("/accounts")
    public ApiResponse<AccountDeleteResponse> delete(@RequestBody @Valid AccountDeleteRequest accountDeleteRequest, @AuthenticationPrincipal LoginAccount loginAccount) {
        return ApiResponse.success(accountService.delete(accountDeleteRequest, loginAccount.getId()));
    }

    @PutMapping("/accounts")
    public ApiResponse<AccountUpdateResponse> update(@RequestBody @Valid AccountUpdateRequest accountUpdateRequest, @AuthenticationPrincipal LoginAccount loginAccount) {
        return ApiResponse.success(accountService.updateAccount(accountUpdateRequest, loginAccount.getId()));
    }

    @GetMapping("/accounts")
    public ApiResponse<AccountResponse> get(@AuthenticationPrincipal LoginAccount loginAccount) {
        return ApiResponse.success(accountService.getAccount(loginAccount.getId()));
    }

}
