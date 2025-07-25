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

    /**
     * Creates an AccountSignupResponse DTO from the given Account entity.
     *
     * @param account the Account entity to extract response data from
     * @return an AccountSignupResponse populated with the account's details
     */
    public static AccountSignupResponse from(Account account) {
        return AccountSignupResponse.builder()
            .id(account.getId())
            .name(account.getName())
            .email(account.getEmail())
            .role(account.getRole().getDescription())
            .subscriptionType(account.getSubscriptionType().getType())
            .createdAt(account.getCreatedAt())
            .build();
    }

}
