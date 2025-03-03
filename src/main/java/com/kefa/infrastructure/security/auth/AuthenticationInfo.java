package com.kefa.infrastructure.security.auth;

import com.kefa.domain.type.Role;
import lombok.Builder;
import lombok.Getter;
import org.springframework.security.core.Authentication;

@Getter
@Builder
public class AuthenticationInfo {

    private Long id;
    private Role role;

    public static AuthenticationInfo from(Authentication authentication) {
        return AuthenticationInfo.builder()
            .id(Long.parseLong(authentication.getName()))
            .role(Role.valueOf(
                authentication.getAuthorities().iterator().next()
                    .getAuthority().replace("ROLE_", "")
            ))
            .build();
    }

}
