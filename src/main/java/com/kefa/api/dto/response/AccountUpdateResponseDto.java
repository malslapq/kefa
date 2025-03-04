package com.kefa.api.dto.response;

import com.kefa.domain.entity.Account;
import com.kefa.domain.type.SubscriptionType;
import lombok.*;

import java.time.LocalDateTime;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class AccountUpdateResponseDto {

    private Long id;
    private String email;
    private String name;
    private SubscriptionType subscriptionType;
    private LocalDateTime updatedAt;

    public static AccountUpdateResponseDto from(Account account) {
        return AccountUpdateResponseDto.builder()
            .id(account.getId())
            .email(account.getEmail())
            .name(account.getName())
            .subscriptionType(account.getSubscriptionType())
            .updatedAt(account.getUpdatedAt())
            .build();
    }

}
