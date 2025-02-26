package com.kefa.api.controller;

import com.kefa.api.dto.request.AccountSignupRequestDto;
import com.kefa.api.dto.response.AccountSignupResponseDto;
import com.kefa.application.service.AccountService;
import com.kefa.common.response.ApiResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class AccountController {

    private final AccountService accountService;

    @PostMapping("/auth/signup")
    public ApiResponse<AccountSignupResponseDto> addAccount(@RequestBody @Valid AccountSignupRequestDto accountSignupRequestDto) {

        return ApiResponse.success(accountService.signup(accountSignupRequestDto));

    }

}
