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

    private RefreshToken createRefreshTokenEntity(String refreshToken, String deviceId) {
        return RefreshToken.builder()
            .token(refreshToken)
            .deviceId(deviceId)
            .expiresAt(jwtProvider.getTokenExpiration(refreshToken))
            .build();
    }

    private TokenResponse issueJwt(Long accountId, Role role, String name) {

        return TokenResponse.builder()
            .accessToken(jwtProvider.createAccessToken(accountId, role, name))
            .refreshToken(jwtProvider.createRefreshToken(accountId, role, name))
            .build();
    }

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
