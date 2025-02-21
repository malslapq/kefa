package com.kefa.infrastructure.security.auth;

import com.kefa.domain.entity.Account;
import lombok.Getter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.Collections;

@Getter
public class CustomUserDetails implements UserDetails {
    private final Long id;
    private final String name;
    private final String email;
    private final String password;
    private final Collection<? extends GrantedAuthority> authorities;
    private final String subscriptionType;
    private final LocalDateTime createdAt;
    private final LocalDateTime updatedAt;

    public CustomUserDetails(Account account) {
        this.id = account.getId();
        this.name = account.getName();
        this.email = account.getEmail();
        this.password = account.getPassword();
        this.subscriptionType = account.getSubscription_type().toString();
        this.authorities = Collections.singleton(
            new SimpleGrantedAuthority("ROLE_" + account.getRole().name())
        );
        this.createdAt = account.getCreatedAt();
        this.updatedAt = account.getUpdatedAt();
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return authorities;
    }

    @Override
    public String getPassword() {
        return password;
    }

    @Override
    public String getUsername() {
        return String.valueOf(id);
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }
}
