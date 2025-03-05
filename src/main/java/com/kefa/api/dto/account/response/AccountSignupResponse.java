package com.kefa.api.dto.account.response;

import com.kefa.domain.entity.Account;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class AccountSignupResponse {

    private Long id;
    private String name;
    private String email;
    private String role;
    private String subscriptionType;
    private LocalDateTime createdAt;

    public static AccountSignupResponse from(Account account) {
        return AccountSignupResponse.builder()
            .id(account.getId())
            .name(account.getName())
            .email(account.getEmail())
            .role(account.getRole().getRole())
            .subscriptionType(account.getSubscriptionType().getType())
            .createdAt(account.getCreatedAt())
            .build();
    }

}
