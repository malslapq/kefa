package com.kefa.infrastructure.security.auth;

import lombok.Builder;
import lombok.Getter;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;

import java.util.Collection;

@Getter
public class JwtAuthenticationToken extends AbstractAuthenticationToken {

    private final LoginAccount principal;
    private final Object credentials;

    @Builder
    public JwtAuthenticationToken(LoginAccount principal, Object credentials, Collection<? extends GrantedAuthority> authorities) {
        super(authorities);
        this.principal = principal;
        this.credentials = credentials;
        setAuthenticated(true);
    }

    @Override
    public Object getPrincipal() {
        return this.principal;
    }
}
