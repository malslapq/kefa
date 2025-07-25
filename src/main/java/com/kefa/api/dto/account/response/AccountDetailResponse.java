package com.kefa.api.dto.account.response;

import com.kefa.domain.entity.Account;
import com.kefa.common.type.LoginType;
import com.kefa.common.type.Role;
import com.kefa.common.type.SubscriptionType;
import com.kefa.domain.entity.SocialInfo;
import lombok.*;

import java.time.LocalDateTime;
import java.util.List;
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
    private List<SocialInfoDto> SocialInfos;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    /**
     * Creates an AccountDetailResponse DTO from the given Account entity.
     *
     * Maps all relevant account fields and transforms the list of SocialInfo entities into SocialInfoDto objects for inclusion in the response.
     *
     * @param account the Account entity to convert
     * @return an AccountDetailResponse populated with details from the provided Account
     */
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
            .SocialInfos(account.getSocialInfos().stream().map(socialInfo -> new SocialInfoDto(socialInfo.getLoginType())).toList())
            .build();
    }

}
