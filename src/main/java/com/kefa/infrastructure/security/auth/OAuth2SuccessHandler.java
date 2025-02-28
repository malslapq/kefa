package com.kefa.infrastructure.security.auth;

import com.kefa.common.exception.AuthenticationException;
import com.kefa.common.exception.ErrorCode;
import com.kefa.domain.type.Role;
import com.kefa.api.dto.response.TokenResponse;
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

import java.io.IOException;

@Component
@RequiredArgsConstructor
public class OAuth2SuccessHandler extends SimpleUrlAuthenticationSuccessHandler {

    private final JwtProvider jwtProvider;

    @Value("${oauth2.main-page-uri}")
    private String mainPageUri;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException {
        OAuth2User oAuth2User = (OAuth2User) authentication.getPrincipal();

        try {
            String roleString = authentication.getAuthorities().stream()
                .findFirst()
                .map(GrantedAuthority::getAuthority)
                .orElseThrow(() -> new AuthenticationException(ErrorCode.ACCESS_DENIED))
                .replace("ROLE_", "");
            Role role = Role.valueOf(roleString);

            Long accountId = oAuth2User.getAttribute("accountId");

            TokenResponse tokenResponse = createJwtToken(accountId, role);
            addTokenCookie(response, tokenResponse);

            getRedirectStrategy().sendRedirect(request, response, mainPageUri);

        } catch (Exception e) {
            response.sendRedirect(mainPageUri);
        }
    }

    private TokenResponse createJwtToken(Long accountId, Role role) {
        String accessToken = jwtProvider.createAccessToken(accountId, role);
        String refreshToken = jwtProvider.createRefreshToken(accountId, role);

        return TokenResponse.builder()
            .accessToken(accessToken)
            .refreshToken(refreshToken)
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
