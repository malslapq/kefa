package com.kefa.api.controller;

import com.kefa.api.dto.request.AccountLoginRequestDto;
import com.kefa.api.dto.request.AccountSignupRequestDto;
import com.kefa.api.dto.response.AccountSignupResponseDto;
import com.kefa.api.dto.response.TokenResponse;
import com.kefa.application.service.AccountService;
import com.kefa.common.response.ApiResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequiredArgsConstructor
public class AccountController {

    private final AccountService accountService;

    @PostMapping("/auth/signup")
    public ApiResponse<AccountSignupResponseDto> addAccount(@RequestBody @Valid AccountSignupRequestDto accountSignupRequestDto) {
        return ApiResponse.success(accountService.signup(accountSignupRequestDto));
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
    public ApiResponse<TokenResponse> login(@RequestBody @Valid AccountLoginRequestDto accountLoginRequestDto,
                                            HttpServletRequest request
    ) {

        String userAgent = request.getHeader("User-Agent");
        accountLoginRequestDto.setDeviceId(generateDeviceId(userAgent));

        return ApiResponse.success(accountService.login(accountLoginRequestDto));
    }

    private String generateDeviceId(String userAgent) {
        return UUID.nameUUIDFromBytes(userAgent.getBytes()).toString();
    }

}
