package com.kefa.domain.vo;

import com.kefa.domain.entity.Account;
import com.kefa.common.type.LoginType;
import com.kefa.common.type.Role;
import com.kefa.common.type.SubscriptionType;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.Set;

@Getter
public class AccountVO {

    private final Long id;
    private final String email;
    private final String name;
    private final SubscriptionType subscriptionType;
    private final Role role;
    private final LocalDateTime createdAt;
    private final LocalDateTime updatedAt;

    @Builder
    private AccountVO(Long id, String email, String name,
                      SubscriptionType subscriptionType, Role role,
                      Set<LoginType> loginTypes,
                      LocalDateTime createdAt, LocalDateTime updatedAt) {
        this.id = id;
        this.email = email;
        this.name = name;
        this.subscriptionType = subscriptionType;
        this.role = role;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    public static AccountVO from(Account account) {
        return AccountVO.builder()
            .id(account.getId())
            .email(account.getEmail())
            .name(account.getName())
            .subscriptionType(account.getSubscriptionType())
            .role(account.getRole())
            .createdAt(account.getCreatedAt())
            .updatedAt(account.getUpdatedAt())
            .build();
    }
}