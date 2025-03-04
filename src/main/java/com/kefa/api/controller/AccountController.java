package com.kefa.api.controller;

import com.kefa.api.dto.request.*;
import com.kefa.api.dto.response.*;
import com.kefa.application.service.AccountService;
import com.kefa.common.response.ApiResponse;
import com.kefa.infrastructure.security.auth.AuthenticationInfo;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequiredArgsConstructor
public class AccountController {

    private final AccountService accountService;

    @DeleteMapping("/account")
    public ApiResponse<AccountDeleteResponse> delete(AccountDeleteRequest accountDeleteRequest, Authentication authentication){
        return ApiResponse.success(accountService.delete(accountDeleteRequest, AuthenticationInfo.from(authentication)));
    }

    @PutMapping("/account/password")
    public ApiResponse<AccountUpdatePasswordResponseDto> updatePassword(@RequestBody @Valid AccountUpdatePasswordRequest accountUpdatePasswordRequest, Authentication authentication){
        return ApiResponse.success(accountService.updatePassword(accountUpdatePasswordRequest, AuthenticationInfo.from(authentication)));
    }

    @PutMapping("/account")
    public ApiResponse<AccountUpdateResponse> update(@RequestBody @Valid AccountUpdateRequest accountUpdateRequest, Authentication authentication){
        return ApiResponse.success(accountService.updateAccount(accountUpdateRequest, AuthenticationInfo.from(authentication)));
    }

    @GetMapping("/account")
    public ApiResponse<AccountResponse> get(Authentication authentication) {
        return ApiResponse.success(accountService.getAccount(AuthenticationInfo.from(authentication)));
    }

    @PostMapping("/auth/signup")
    public ApiResponse<AccountSignupResponse> join(@RequestBody @Valid AccountSignupRequest accountSignupRequest) {
        return ApiResponse.success(accountService.signup(accountSignupRequest));
    }

    @GetMapping("/auth/email-verify")
    public void emailVerify(@RequestParam String token, HttpServletResponse response) {
        accountService.emailVerify(token);
        response.setStatus(HttpStatus.FOUND.value());
        response.setHeader("Location", "http://localhost:8080/index");
    }

    @PostMapping("/auth/email-verify/resend")
    public ApiResponse<Void> resendEmailVerification(@RequestParam String email) {
        accountService.resendVerificationEmail(email);
        return ApiResponse.success();
    }

    @PostMapping("/auth/login")
    public ApiResponse<TokenResponse> login(@RequestBody @Valid AccountLoginRequest accountLoginRequest,
                                            HttpServletRequest request
    ) {

        String userAgent = request.getHeader("User-Agent");
        accountLoginRequest.setDeviceId(generateDeviceId(userAgent));

        return ApiResponse.success(accountService.login(accountLoginRequest));
    }

    private String generateDeviceId(String userAgent) {
        return UUID.nameUUIDFromBytes(userAgent.getBytes()).toString();
    }

}
