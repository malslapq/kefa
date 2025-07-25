package com.kefa.application.service;

import com.kefa.api.dto.account.request.AccountLoginRequest;
import com.kefa.api.dto.account.request.AccountSignupRequest;
import com.kefa.api.dto.account.request.AccountUpdatePasswordRequest;
import com.kefa.api.dto.account.response.AccountSignupResponse;
import com.kefa.api.dto.account.response.AccountUpdatePasswordResponse;
import com.kefa.api.dto.auth.request.PasswordResetDto;
import com.kefa.api.dto.auth.request.PasswordResetRequestDto;
import com.kefa.api.dto.auth.response.TokenResponse;
import com.kefa.application.usecase.AuthenticationUseCase;
import com.kefa.application.usecase.EmailVerificationUseCase;
import com.kefa.application.usecase.PasswordResetEmailSenderUseCase;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final AuthenticationUseCase authenticationUseCase;
    private final EmailVerificationUseCase emailVerificationUseCase;
    private final PasswordResetEmailSenderUseCase passwordResetEmailSenderUseCase;

    /**
     * Resets the user's password using the provided password reset request data.
     *
     * @param request the password reset request containing necessary information for resetting the password
     */
    public void passwordReset(PasswordResetDto request) {
        authenticationUseCase.passwordReset(request);
    }

    /**
     * Validates a password reset request and sends a password reset email to the specified address.
     *
     * This method ensures the provided email is eligible for password reset and then triggers the sending of a password reset email.
     *
     * @param passwordResetRequestDto the request containing the email address for password reset
     */
    @Transactional
    public void sendPasswordResetEmail(PasswordResetRequestDto passwordResetRequestDto) {
        authenticationUseCase.validatePasswordReset(passwordResetRequestDto.getEmail());
        passwordResetEmailSenderUseCase.sendPasswordResetEmail(passwordResetRequestDto.getEmail());
    }

    /**
     * Generates a new authentication token using the provided refresh token and device ID.
     *
     * @param refreshToken the refresh token to be validated and exchanged
     * @param deviceId the identifier of the device requesting the token refresh
     * @return a new {@link TokenResponse} containing refreshed authentication tokens
     */
    public TokenResponse refreshToken(String refreshToken, String deviceId) {
        return authenticationUseCase.refreshToken(refreshToken, deviceId);
    }

    public void emailVerify(String token) {
        emailVerificationUseCase.verify(token);
    }

    public void resendVerificationEmail(String email) {
        emailVerificationUseCase.resendEmail(email);
    }

    public AccountUpdatePasswordResponse updatePassword(AccountUpdatePasswordRequest accountUpdatePasswordRequest, Long loginAccountId) {
        return authenticationUseCase.updatePassword(accountUpdatePasswordRequest, loginAccountId);
    }

    public TokenResponse login(AccountLoginRequest accountLoginRequest) {
        return authenticationUseCase.login(accountLoginRequest);
    }

    public void logout(Long loginAccountId, String jwtId) {
        authenticationUseCase.logout(loginAccountId, jwtId);
    }

    @Transactional
    public AccountSignupResponse signup(AccountSignupRequest request) {

        AccountSignupResponse response = authenticationUseCase.signup(request);
        emailVerificationUseCase.sendVerificationEmail(request.getEmail());

        return response;

    }
}
