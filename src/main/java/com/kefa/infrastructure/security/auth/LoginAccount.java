package com.kefa.infrastructure.security.auth;


import com.kefa.domain.type.Role;
import lombok.*;

@AllArgsConstructor(access = AccessLevel.PROTECTED)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Builder
@Getter
public class LoginAccount {

    private Long id;
    private Role role;

    public static LoginAccount of(final Long id, final Role role) {
        return LoginAccount.builder()
            .id(id)
            .role(role)
            .build();
    }

}
