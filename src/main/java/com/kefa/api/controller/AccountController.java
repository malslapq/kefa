package com.kefa.api.controller;

import com.kefa.api.dto.account.request.*;
import com.kefa.api.dto.account.response.*;
import com.kefa.application.service.AccountService;
import com.kefa.common.response.ApiResponse;
import com.kefa.infrastructure.security.auth.LoginAccount;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

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

    @PutMapping("/accounts/password")
    public ApiResponse<AccountUpdatePasswordResponse> updatePassword(@RequestBody @Valid AccountUpdatePasswordRequest accountUpdatePasswordRequest, @AuthenticationPrincipal LoginAccount loginAccount) {
        return ApiResponse.success(accountService.updatePassword(accountUpdatePasswordRequest, loginAccount.getId()));
    }

    @PutMapping("/accounts")
    public ApiResponse<AccountUpdateResponse> update(@RequestBody @Valid AccountUpdateRequest accountUpdateRequest, @AuthenticationPrincipal LoginAccount loginAccount) {
        return ApiResponse.success(accountService.updateAccount(accountUpdateRequest, loginAccount.getId()));
    }

    @GetMapping("/accounts")
    public ApiResponse<AccountResponse> get(@AuthenticationPrincipal LoginAccount loginAccount) {
        return ApiResponse.success(accountService.getAccount(loginAccount.getId()));
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
    public ApiResponse<String> resendEmailVerification(@RequestParam String email) {
        accountService.resendVerificationEmail(email);
        return ApiResponse.success("메일을 재전송됐습니다.");
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
