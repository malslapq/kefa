package com.kefa.api.controller;

import com.kefa.api.dto.account.request.AccountLoginRequest;
import com.kefa.api.dto.account.request.AccountSignupRequest;
import com.kefa.api.dto.account.request.AccountUpdatePasswordRequest;
import com.kefa.api.dto.account.response.AccountSignupResponse;
import com.kefa.api.dto.account.response.AccountUpdatePasswordResponse;
import com.kefa.api.dto.auth.request.PasswordResetDto;
import com.kefa.api.dto.auth.request.PasswordResetRequestDto;
import com.kefa.api.dto.auth.response.TokenResponse;
import com.kefa.application.service.AuthService;
import com.kefa.common.response.ApiResponse;
import com.kefa.infrastructure.security.auth.LoginAccount;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;

import static com.kefa.common.util.RequestUtils.generateDeviceIdFromRequest;

@RestController
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    /**
     * Handles password reset requests by updating the user's password.
     *
     * Accepts a validated password reset payload and delegates the reset operation to the authentication service.
     * Returns a success response upon completion.
     */
    @PostMapping("/auth/password-reset")
    public ApiResponse<Void> passwordReset(@RequestBody @Valid PasswordResetDto request) {
        authService.passwordReset(request);
        return ApiResponse.success();
    }

    /**
     * Initiates a password reset process by sending a password reset email to the user.
     *
     * @param passwordResetRequestDto the request containing the user's email address for password reset
     * @return a success response indicating the email was sent
     */
    @PostMapping("/auth/password-reset-request")
    public ApiResponse<Void> passwordResetRequest(@RequestBody @Valid PasswordResetRequestDto passwordResetRequestDto) {
        authService.sendPasswordResetEmail(passwordResetRequestDto);
        return ApiResponse.success();
    }

    /**
     * Returns the currently authenticated user's account details.
     *
     * @return an API response containing the authenticated user's information
     */
    @GetMapping("/auth/check")
    public ApiResponse<LoginAccount> checkAuth(@AuthenticationPrincipal LoginAccount loginAccount) {
        return ApiResponse.success(loginAccount);
    }

    /**
     * Refreshes the authentication token using the "refreshToken" cookie and device ID from the request.
     *
     * @param request the HTTP request containing the "refreshToken" cookie
     * @return a response containing the new authentication tokens
     */
    @PostMapping("/auth/token/refresh")
    public ApiResponse<TokenResponse> refreshToken(HttpServletRequest request) {

        String deviceId = generateDeviceIdFromRequest(request);
        Cookie cookie = Arrays.stream(request.getCookies()).filter(o -> o.getName().equals("refreshToken")).findFirst().orElseThrow();
        String refreshToken = cookie.getValue();

        return ApiResponse.success(authService.refreshToken(refreshToken, deviceId));
    }

    /**
     * Updates the authenticated user's password.
     *
     * @param accountUpdatePasswordRequest the request containing the current and new password details
     * @param loginAccount the currently authenticated user
     * @return the response containing the result of the password update operation
     */
    @PutMapping("/auth/password")
    public ApiResponse<AccountUpdatePasswordResponse> updatePassword(@RequestBody @Valid AccountUpdatePasswordRequest accountUpdatePasswordRequest,
                                                                     @AuthenticationPrincipal LoginAccount loginAccount) {
        return ApiResponse.success(authService.updatePassword(accountUpdatePasswordRequest, loginAccount.getId()));
    }

    @PostMapping("/auth/signup")
    public ApiResponse<AccountSignupResponse> join(@RequestBody @Valid AccountSignupRequest accountSignupRequest) {
        return ApiResponse.success(authService.signup(accountSignupRequest));
    }

    /**
     * Verifies a user's email using the provided token and redirects to the frontend application.
     *
     * After successful verification, responds with an HTTP 302 redirect to "http://localhost:3000/".
     *
     * @param token the email verification token
     * @param response the HTTP response used to set the redirect
     */
    @GetMapping("/auth/email-verify")
    public void emailVerify(@RequestParam String token, HttpServletResponse response) {
        authService.emailVerify(token);
        response.setStatus(HttpStatus.FOUND.value());
        response.setHeader("Location", "http://localhost:3000/");
    }

    /**
     * Resends the email verification message to the specified email address.
     *
     * @param email the email address to which the verification email will be resent
     * @return a success response indicating the email was resent
     */
    @PostMapping("/auth/email-verify/resend")
    public ApiResponse<String> resendEmailVerification(@RequestParam String email) {
        authService.resendVerificationEmail(email);
        return ApiResponse.success("메일을 재전송됐습니다.");
    }

    @PostMapping("/auth/login")
    public ApiResponse<TokenResponse> login(@RequestBody @Valid AccountLoginRequest accountLoginRequest,
                                            HttpServletRequest request, HttpServletResponse response) {

        String deviceId = generateDeviceIdFromRequest(request);
        accountLoginRequest.setDeviceId(deviceId);
        TokenResponse tokenResponse = authService.login(accountLoginRequest);
        addTokenCookie(response, tokenResponse);

        return ApiResponse.success(tokenResponse);
    }

    /**
     * Logs out the authenticated user and removes the refresh token cookie from the response.
     *
     * @return a success response indicating the user has been logged out
     */
    @PostMapping("/auth/logout")
    public ApiResponse<?> logout(@AuthenticationPrincipal LoginAccount loginAccount, HttpServletResponse response) {

        authService.logout(loginAccount.getId(), loginAccount.getJwtId());
        removeTokenCookie(response);

        return ApiResponse.success();
    }

    /**
     * Adds a secure, HTTP-only "refreshToken" cookie with the provided token value to the HTTP response.
     *
     * The cookie is set with path "/", a max age of 3600 seconds, and is marked as secure and HTTP-only.
     */
    private void addTokenCookie(HttpServletResponse response, TokenResponse tokenResponse) {
        Cookie refreshTokenCookie = new Cookie("refreshToken", tokenResponse.getRefreshToken());
        refreshTokenCookie.setHttpOnly(true);
        refreshTokenCookie.setSecure(true);
        refreshTokenCookie.setPath("/");
        refreshTokenCookie.setMaxAge(3600);

        response.addCookie(refreshTokenCookie);
    }

    /**
     * Removes the "refreshToken" cookie from the HTTP response by setting its value to empty and max age to zero.
     *
     * @param response the HTTP response to which the removal cookie is added
     */
    private void removeTokenCookie(HttpServletResponse response) {
        Cookie refreshTokenCookie = new Cookie("refreshToken", "");
        refreshTokenCookie.setHttpOnly(true);
        refreshTokenCookie.setSecure(true);
        refreshTokenCookie.setPath("/");
        refreshTokenCookie.setMaxAge(0);

        response.addCookie(refreshTokenCookie);
    }

}
