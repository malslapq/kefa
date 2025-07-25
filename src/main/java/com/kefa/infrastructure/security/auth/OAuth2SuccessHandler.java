package com.kefa.infrastructure.security.auth;

import com.kefa.api.dto.auth.response.TokenResponse;
import com.kefa.common.exception.AuthenticationException;
import com.kefa.common.exception.ErrorCode;
import com.kefa.domain.entity.Account;
import com.kefa.domain.entity.RefreshToken;
import com.kefa.common.type.Role;
import com.kefa.infrastructure.repository.AccountRepository;
import com.kefa.infrastructure.repository.ActiveTokenRepository;
import com.kefa.infrastructure.repository.RefreshTokenRepository;
import com.kefa.infrastructure.security.jwt.JwtProvider;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.time.Duration;
import java.time.LocalDateTime;

import static com.kefa.common.util.RequestUtils.generateDeviceIdFromRequest;

@Component
@RequiredArgsConstructor
public class OAuth2SuccessHandler extends SimpleUrlAuthenticationSuccessHandler {

    private final JwtProvider jwtProvider;
    private final AccountRepository accountRepository;
    private final ActiveTokenRepository activeTokenRepository;
    private final RefreshTokenRepository refreshTokenRepository;

    @Value("${oauth2.login-success-redirect-url}")
    private String redirectUrl;

    /****
     * Handles successful OAuth2 authentication by issuing JWT tokens, persisting refresh token information, and redirecting the user.
     *
     * On successful authentication, generates access and refresh tokens for the authenticated user, stores or updates the refresh token entity associated with the user's account, saves the active access token, and sets the refresh token as a secure HTTP-only cookie in the response. Redirects the user to the configured success URL. If any error occurs during this process, redirects to the same URL without propagating the error.
     *
     * @param request the HTTP request containing authentication details
     * @param response the HTTP response to which cookies and redirects are added
     * @param authentication the authentication object representing the authenticated user
     * @throws IOException if an input or output exception occurs during redirection
     */
    @Override
    @Transactional
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException {
        OAuth2User oAuth2User = (OAuth2User) authentication.getPrincipal();

        try {
            String roleName = authentication.getAuthorities().stream()
                .findFirst()
                .map(GrantedAuthority::getAuthority)
                .orElseThrow(() -> new AuthenticationException(ErrorCode.ACCESS_DENIED))
                .replace("ROLE_", "");
            Long accountId = oAuth2User.getAttribute("accountId");
            Role role = Role.from(roleName);

            TokenResponse tokenResponse = issueJwt(accountId, role, oAuth2User.getName());
            addTokenCookie(response, tokenResponse);

            Account account = accountRepository.findByIdWithRefreshToken(accountId).orElseThrow(() -> new AuthenticationException(ErrorCode.NOT_FOUND_ACCOUNT));
            String deviceId = generateDeviceIdFromRequest(request);
            RefreshToken refreshToken = account.getRefreshToken();

            if (refreshToken != null) {

                refreshToken.updateToken(tokenResponse.getRefreshToken(), jwtProvider.getTokenExpiration(tokenResponse.getRefreshToken()));
                refreshToken.updateDeviceId(deviceId);

            } else {

                refreshToken = createRefreshTokenEntity(tokenResponse.getRefreshToken(), deviceId);
                account.addRefreshToken(refreshToken);
                refreshTokenRepository.save(refreshToken);

            }

            String jwtId = jwtProvider.getJwtId(tokenResponse.getAccessToken());
            activeTokenRepository.save(account.getId(), jwtId);

            getRedirectStrategy().sendRedirect(request, response, redirectUrl);


        } catch (Exception e) {
            response.sendRedirect(redirectUrl);
        }
    }

    /**
     * Creates a new RefreshToken entity with the specified token value and device ID.
     *
     * The expiration time is determined by extracting it from the provided refresh token.
     *
     * @param refreshToken the refresh token string to associate with the entity
     * @param deviceId the identifier of the device for which the token is issued
     * @return a new RefreshToken entity with the given token, device ID, and expiration time
     */
    private RefreshToken createRefreshTokenEntity(String refreshToken, String deviceId) {
        return RefreshToken.builder()
            .token(refreshToken)
            .deviceId(deviceId)
            .expiresAt(jwtProvider.getTokenExpiration(refreshToken))
            .build();
    }

    /**
     * Generates and returns a TokenResponse containing access and refresh tokens for the specified account, role, and username.
     *
     * @param accountId the unique identifier of the account
     * @param role the user's role
     * @param name the username or display name
     * @return a TokenResponse with newly issued access and refresh tokens
     */
    private TokenResponse issueJwt(Long accountId, Role role, String name) {

        return TokenResponse.builder()
            .accessToken(jwtProvider.createAccessToken(accountId, role, name))
            .refreshToken(jwtProvider.createRefreshToken(accountId, role, name))
            .build();
    }

    /**
     * Adds a secure, HTTP-only refresh token cookie to the HTTP response with an expiration matching the token's validity period.
     *
     * @param response the HTTP response to which the cookie will be added
     * @param tokenResponse the token response containing the refresh token
     */
    private void addTokenCookie(HttpServletResponse response, TokenResponse tokenResponse) {
        int maxAgeInSeconds = (int) Duration.between(LocalDateTime.now(), jwtProvider.getTokenExpiration(tokenResponse.getRefreshToken())).getSeconds();
        Cookie refreshTokenCookie = new Cookie("refreshToken", tokenResponse.getRefreshToken());
        refreshTokenCookie.setHttpOnly(true);
        refreshTokenCookie.setSecure(true);
        refreshTokenCookie.setPath("/");
        refreshTokenCookie.setMaxAge(maxAgeInSeconds);

        response.addCookie(refreshTokenCookie);
    }

}
