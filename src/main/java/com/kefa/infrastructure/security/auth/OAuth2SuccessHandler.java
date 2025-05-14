package com.kefa.infrastructure.security.auth;

import com.kefa.api.dto.auth.response.TokenResponse;
import com.kefa.common.exception.AuthenticationException;
import com.kefa.common.exception.ErrorCode;
import com.kefa.domain.entity.Account;
import com.kefa.domain.entity.RefreshToken;
import com.kefa.domain.type.Role;
import com.kefa.infrastructure.repository.AccountRepository;
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

import static com.kefa.common.util.RequestUtils.generateDeviceIdFromRequest;

@Component
@RequiredArgsConstructor
public class OAuth2SuccessHandler extends SimpleUrlAuthenticationSuccessHandler {

    private final JwtProvider jwtProvider;
    private final AccountRepository accountRepository;

    @Value("${oauth2.main-page-uri}")
    private String mainPageUri;

    @Override
    @Transactional
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException {
        OAuth2User oAuth2User = (OAuth2User) authentication.getPrincipal();

        try {
            String roleString = authentication.getAuthorities().stream()
                .findFirst()
                .map(GrantedAuthority::getAuthority)
                .orElseThrow(() -> new AuthenticationException(ErrorCode.ACCESS_DENIED))
                .replace("ROLE_", "");
            Long accountId = oAuth2User.getAttribute("accountId");
            Role role = Role.valueOf(roleString);

            TokenResponse tokenResponse = issueJwt(accountId, role, oAuth2User.getName());
            addTokenCookie(response, tokenResponse);

            Account account = accountRepository.findByIdWithRefreshToken(accountId).orElseThrow(() -> new AuthenticationException(ErrorCode.NOT_FOUND_ACCOUNT));
            String deviceId = generateDeviceIdFromRequest(request);
            RefreshToken savedRefreshToken = account.getRefreshToken();

            if (savedRefreshToken != null) {
                savedRefreshToken.updateToken(tokenResponse.getRefreshToken(), jwtProvider.getTokenExpiration(tokenResponse.getRefreshToken()));
                savedRefreshToken.updateDeviceId(deviceId);
            } else {
                account.addRefreshToken(createRefreshTokenEntity(account, deviceId, tokenResponse.getRefreshToken()));
            }

            getRedirectStrategy().sendRedirect(request, response, mainPageUri);

        } catch (Exception e) {
            response.sendRedirect(mainPageUri);
        }
    }

    private RefreshToken createRefreshTokenEntity(Account account, String refreshToken, String deviceId) {

        return RefreshToken.builder()
            .token(refreshToken)
            .account(account)
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
        Cookie accessTokenCookie = new Cookie("accessToken", tokenResponse.getAccessToken());
        accessTokenCookie.setHttpOnly(true);
        accessTokenCookie.setSecure(true);
        accessTokenCookie.setPath("/");
        accessTokenCookie.setMaxAge(3600);

        response.addCookie(accessTokenCookie);
    }

}
