package com.kefa.infrastructure.security.jwt;

import com.kefa.common.exception.AuthenticationException;
import com.kefa.common.exception.ErrorCode;
import com.kefa.common.type.Role;
import com.kefa.infrastructure.repository.BlackListRepository;
import com.kefa.infrastructure.security.auth.JwtAuthenticationToken;
import com.kefa.infrastructure.security.auth.LoginAccount;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.lang.NonNull;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Collection;
import java.util.Collections;

@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtProvider jwtProvider;
    private final BlackListRepository blackListRepository;

    private static final String AUTHORIZATION_HEADER = "Authorization";
    private static final String BEARER_PREFIX = "Bearer ";

    @Override
    protected void doFilterInternal(@NonNull HttpServletRequest request,@NonNull HttpServletResponse response,@NonNull FilterChain filterChain)

        throws ServletException, IOException {
        String token = getTokenFromRequest(request);

        if (StringUtils.hasText(token) && jwtProvider.validateToken(token)) {
            String jwtId = jwtProvider.getJwtId(token);

            if (blackListRepository.isBlacklisted(jwtId)) {
                throw new AuthenticationException(ErrorCode.BLACKLISTED_TOKEN);
            }

            Authentication authentication = createAuthentication(token);
            SecurityContextHolder.getContext().setAuthentication(authentication);
        }

        filterChain.doFilter(request, response);

    }

    private Authentication createAuthentication(String token) {

        Long id = jwtProvider.getId(token);
        Role role = jwtProvider.getRole(token);
        String name = jwtProvider.getName(token);
        String jwtId = jwtProvider.getJwtId(token);
        LoginAccount loginAccount = LoginAccount.of(id, role, name, jwtId);
        Collection<GrantedAuthority> authorities = createAuthorities(role);

        return new JwtAuthenticationToken(loginAccount, " ", authorities);

    }

    private Collection<GrantedAuthority> createAuthorities(Role role) {
        return Collections.singleton(new SimpleGrantedAuthority("ROLE_" + role.name()));
    }

    private String getTokenFromRequest(HttpServletRequest request) {

        String bearerToken = request.getHeader(AUTHORIZATION_HEADER);
        if (StringUtils.hasText(bearerToken) && bearerToken.startsWith(BEARER_PREFIX)) {
            return bearerToken.substring(7);
        }
        return null;

    }

}
