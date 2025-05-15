package com.kefa.api.dto.account.response;

import com.kefa.domain.entity.Account;
import com.kefa.common.type.LoginType;
import com.kefa.common.type.Role;
import com.kefa.common.type.SubscriptionType;
import lombok.*;

import java.time.LocalDateTime;
import java.util.Set;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class AccountDetailResponse {

    private Long id;
    private String email;
    private String name;
    private SubscriptionType subscriptionType;
    private Role role;
    private boolean emailVerified;
    private Set<LoginType> loginTypes;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public static AccountDetailResponse from(Account account) {
        return AccountDetailResponse.builder()
            .id(account.getId())
            .email(account.getEmail())
            .name(account.getName())
            .subscriptionType(account.getSubscriptionType())
            .role(account.getRole())
            .emailVerified(account.isEmailVerified())
            .createdAt(account.getCreatedAt())
            .updatedAt(account.getUpdatedAt())
            .build();
    }

}
