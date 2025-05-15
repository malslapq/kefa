package com.kefa.api.controller;

import com.kefa.api.dto.account.command.GetAccountsCommand;
import com.kefa.api.dto.account.response.AccountDetailResponse;
import com.kefa.api.dto.consulting.response.PagedResponse;
import com.kefa.application.service.AdminService;
import com.kefa.common.response.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@PreAuthorize("hasAnyRole('ADMIN')")
@RestController("/admin")
@RequiredArgsConstructor
public class AdminController {

    private final AdminService adminService;

    @GetMapping("/accounts")
    public ApiResponse<PagedResponse<AccountDetailResponse>> getAccounts(@RequestParam String keyword,
                                                                         @RequestParam String searchType,
                                                                         @RequestParam(defaultValue = "0") int page,
                                                                         @RequestParam(defaultValue = "10") int size) {

        GetAccountsCommand command = GetAccountsCommand.builder()
            .keyword(keyword)
            .searchType(searchType)
            .page(page)
            .size(size)
            .build();

        return ApiResponse.success(adminService.getAccounts(command));
    }

}
