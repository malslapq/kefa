package com.kefa.api.controller;

import com.kefa.api.dto.account.request.AccountUpdateSubscriptionTypeRequest;
import com.kefa.api.dto.account.command.AccountsGetCommand;
import com.kefa.api.dto.account.request.AccountUpdatePasswordRequestFromAdmin;
import com.kefa.api.dto.account.request.AccountUpdateRequest;
import com.kefa.api.dto.account.request.AccountUpdateRoleRequest;
import com.kefa.api.dto.account.response.AccountDetailResponse;
import com.kefa.api.dto.consulting.response.PagedResponse;
import com.kefa.application.service.AdminService;
import com.kefa.common.response.ApiResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RequestMapping("/admin")
@PreAuthorize("hasAnyRole('ADMIN')")
@RestController
@RequiredArgsConstructor
public class AdminController {

    private final AdminService adminService;

    @DeleteMapping("/account/{accountId}/social-info")
    public ApiResponse<Void> deleteAccountSocialInfo(@PathVariable("accountId") Long accountId) {
        adminService.deleteAccountSocialInfo(accountId);
        return ApiResponse.success();
    }

    @DeleteMapping("/account/{accountId}")
    public ApiResponse<Void> deleteAccount(@PathVariable("accountId") Long accountId) {
        adminService.deleteAccount(accountId);
        return ApiResponse.success();
    }

    @PutMapping("/account/{accountId}/role")
    public ApiResponse<AccountDetailResponse> updateAccountRole(@PathVariable("accountId") Long accountId, @RequestBody @Valid AccountUpdateRoleRequest request) {
        return ApiResponse.success(adminService.updateAccountRole(accountId, request));
    }

    @PutMapping("/account/{accountId}/subscription-type")
    public ApiResponse<AccountDetailResponse> updateAccountSubscriptionType(@PathVariable("accountId") Long accountId, @RequestBody @Valid AccountUpdateSubscriptionTypeRequest request) {
        return ApiResponse.success(adminService.updateAccountSubscriptionType(accountId, request));
    }

    @PutMapping("/account/{accountId}/password")
    public ApiResponse<AccountDetailResponse> updateAccountPassword(@PathVariable Long accountId, @RequestBody @Valid AccountUpdatePasswordRequestFromAdmin request) {
        return ApiResponse.success(adminService.updateAccountPassword(accountId, request));
    }

    @PutMapping("/account/{accountId}")
    public ApiResponse<AccountDetailResponse> updateAccount(@PathVariable Long accountId, @RequestBody @Valid AccountUpdateRequest request) {
        return ApiResponse.success(adminService.updateAccount(accountId, request));
    }

    @GetMapping("/accounts")
    public ApiResponse<PagedResponse<AccountDetailResponse>> getAccounts(@RequestParam String keyword,
                                                                         @RequestParam String searchType,
                                                                         @RequestParam(defaultValue = "0") int page,
                                                                         @RequestParam(defaultValue = "10") int size) {

        AccountsGetCommand command = AccountsGetCommand.builder()
            .keyword(keyword)
            .searchType(searchType)
            .page(page)
            .size(size)
            .build();

        return ApiResponse.success(adminService.getAccounts(command));
    }

}
