package com.kefa.api.dto.response;

import com.kefa.domain.entity.Account;
import com.kefa.domain.type.LoginType;
import com.kefa.domain.type.Role;
import com.kefa.domain.type.SubscriptionType;
import lombok.*;

import java.time.LocalDateTime;
import java.util.Set;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class AccountResponse {

    private Long id;
    private String email;
    private String name;
    private SubscriptionType subscriptionType;
    private Role role;
    private boolean emailVerified;
    private Set<LoginType> loginTypes;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public static AccountResponse from(Account account) {
        return AccountResponse.builder()
            .id(account.getId())
            .email(account.getEmail())
            .name(account.getName())
            .subscriptionType(account.getSubscriptionType())
            .role(account.getRole())
            .emailVerified(account.isEmailVerified())
            .loginTypes(account.getLoginTypes())
            .createdAt(account.getCreatedAt())
            .updatedAt(account.getUpdatedAt())
            .build();
    }

}
