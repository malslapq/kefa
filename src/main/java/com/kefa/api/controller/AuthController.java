package com.kefa.api.controller;

import com.kefa.api.dto.account.request.AccountLoginRequest;
import com.kefa.api.dto.account.request.AccountSignupRequest;
import com.kefa.api.dto.account.request.AccountUpdatePasswordRequest;
import com.kefa.api.dto.account.response.AccountSignupResponse;
import com.kefa.api.dto.account.response.AccountUpdatePasswordResponse;
import com.kefa.api.dto.account.response.TokenResponse;
import com.kefa.application.service.AuthService;
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
public class AuthController {

    private final AuthService authService;

    @PutMapping("/accounts/password")
    public ApiResponse<AccountUpdatePasswordResponse> updatePassword(@RequestBody @Valid AccountUpdatePasswordRequest accountUpdatePasswordRequest, @AuthenticationPrincipal LoginAccount loginAccount) {
        return ApiResponse.success(authService.updatePassword(accountUpdatePasswordRequest, loginAccount.getId()));
    }

    @PostMapping("/auth/signup")
    public ApiResponse<AccountSignupResponse> join(@RequestBody @Valid AccountSignupRequest accountSignupRequest) {
        return ApiResponse.success(authService.signup(accountSignupRequest));
    }

    @GetMapping("/auth/email-verify")
    public void emailVerify(@RequestParam String token, HttpServletResponse response) {
        authService.emailVerify(token);
        response.setStatus(HttpStatus.FOUND.value());
        response.setHeader("Location", "http://localhost:8080/index");
    }

    @PostMapping("/auth/email-verify/resend")
    public ApiResponse<String> resendEmailVerification(@RequestParam String email) {
        authService.resendVerificationEmail(email);
        return ApiResponse.success("메일을 재전송됐습니다.");
    }

    @PostMapping("/auth/login")
    public ApiResponse<TokenResponse> login(@RequestBody @Valid AccountLoginRequest accountLoginRequest,
                                            HttpServletRequest request) {

        String userAgent = request.getHeader("User-Agent");
        accountLoginRequest.setDeviceId(generateDeviceId(userAgent));

        return ApiResponse.success(authService.login(accountLoginRequest));
    }

    private String generateDeviceId(String userAgent) {
        return UUID.nameUUIDFromBytes(userAgent.getBytes()).toString();
    }

}
