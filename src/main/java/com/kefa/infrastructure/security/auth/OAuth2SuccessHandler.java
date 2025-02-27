package com.kefa.infrastructure.security.auth;

import com.kefa.common.exception.AuthenticationException;
import com.kefa.common.exception.ErrorCode;
import com.kefa.domain.type.Role;
import com.kefa.infrastructure.security.jwt.JwtToken;
import com.kefa.infrastructure.security.jwt.JwtTokenProvider;
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

    private final JwtTokenProvider jwtTokenProvider;

    @Value("${oauth2.main-page-uri}")
    private String mainPageUri;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException {
        OAuth2User oAuth2User = (OAuth2User) authentication.getPrincipal();

        try {
            String roleString = authentication.getAuthorities().stream()
                .findFirst()
                .map(GrantedAuthority::getAuthority)
                .orElseThrow(() -> new AuthenticationException(ErrorCode.INVALID_ROLE))
                .replace("ROLE_", "");
            Role role = Role.valueOf(roleString);

            Long accountId = oAuth2User.getAttribute("accountId");

            JwtToken jwtToken = createJwtToken(accountId, role);
            addTokenCookie(response, jwtToken);

            getRedirectStrategy().sendRedirect(request, response, mainPageUri);

        } catch (Exception e) {
            response.sendRedirect(mainPageUri);
        }
    }

    private JwtToken createJwtToken(Long accountId, Role role) {
        String accessToken = jwtTokenProvider.createAccessToken(accountId, role);
        String refreshToken = jwtTokenProvider.createRefreshToken(accountId, role);

        return JwtToken.builder()
            .accessToken(accessToken)
            .refreshToken(refreshToken)
            .build();
    }

    private void addTokenCookie(HttpServletResponse response, JwtToken jwtToken) {
        Cookie accessTokenCookie = new Cookie("accessToken", jwtToken.getAccessToken());
        accessTokenCookie.setHttpOnly(true);
        accessTokenCookie.setSecure(true);
        accessTokenCookie.setPath("/");
        accessTokenCookie.setMaxAge(3600);

        response.addCookie(accessTokenCookie);
    }

}
