package com.kefa.infrastructure.security.auth;


import com.kefa.common.type.Role;
import lombok.*;

@AllArgsConstructor(access = AccessLevel.PROTECTED)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Builder
@Getter
public class LoginAccount {

    private Long id;
    private Role role;
    private String name;

    public static LoginAccount of(final Long id, final Role role, final String name) {
        return LoginAccount.builder()
            .id(id)
            .role(role)
            .name(name)
            .build();
    }

}
